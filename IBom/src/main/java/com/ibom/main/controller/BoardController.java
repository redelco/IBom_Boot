package com.ibom.main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.CommentRepository;
import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PostFavoriteRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Comment;
import com.ibom.main.model.Member;
import com.ibom.main.model.Post;
import com.ibom.main.model.PostFavorite;

import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostFavoriteRepository postFavoriteRepository;

    public BoardController(PostRepository postRepository,
                           CommentRepository commentRepository,
                           MemberRepository memberRepository,
                           PostFavoriteRepository postFavoriteRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
        this.postFavoriteRepository = postFavoriteRepository;
    }

    /** 나눔 게시판 목록 + 상세필터 (여러 개 선택 가능) */
    @GetMapping("/board/share")
    public String shareBoard(@RequestParam(value = "itemType",  required = false) List<String> itemType,
                             @RequestParam(value = "category",  required = false) List<String> category,
                             @RequestParam(value = "tradeType", required = false) List<String> tradeType,
                             Model model,
                             HttpSession session) {

        List<String> itemTypes  = clean(itemType);
        List<String> categories = clean(category);
        List<String> tradeTypes = clean(tradeType);

        List<Post> posts = new ArrayList<>();

        for (Post p : postRepository.findByTypeAndIsDeletedFalseOrderByCreatedAtDesc("SHARE")) {
            if (matchesFilter(p, itemTypes, categories, tradeTypes)) {
                posts.add(p);
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("myFavorites", myFavoriteIds(session));

        /* 화면에서 체크 상태를 복원하기 위해 그대로 돌려준다 */
        model.addAttribute("fItemType",  itemTypes);
        model.addAttribute("fCategory",  categories);
        model.addAttribute("fTradeType", tradeTypes);

        return "board/share";
    }
    /** 나눔 상세 */
    @GetMapping("/board/shareDetail")
    public String shareDetail(@RequestParam("id") Long id,
                              Model model,
                              HttpSession session) {

        Post post = postRepository.findById(id).orElse(null);

        if (post == null || Boolean.TRUE.equals(post.getIsDeleted())) {
            return "redirect:/board/share";
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id);

        Long userId = loginUserId(session);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("nicknames", buildNicknameMap(post, comments));
        model.addAttribute("favoriteCount", postFavoriteRepository.countByPostId(id));
        model.addAttribute("favorited",
                userId != null && postFavoriteRepository.existsByUserIdAndPostId(userId, id));

        return "board/shareDetail";
    }

    /* ===================== 도우미 메서드 ===================== */

    /** 체크된 값 중 빈 문자열("전체")을 걸러낸다 */
    private List<String> clean(List<String> values) {

        List<String> result = new ArrayList<>();

        if (values == null) {
            return result;
        }

        for (String v : values) {
            if (v != null && !v.isBlank()) {
                result.add(v);
            }
        }

        return result;
    }

    /**
     * 글이 선택된 필터에 맞는지 본다.
     * 한 항목에서 아무것도 안 골랐으면 그 항목은 통과(= 전체).
     * CATEGORY 는 "유아용품 > 수면" 형태라 앞/뒤로 나눠서 확인한다.
     */
    private boolean matchesFilter(Post post,
                                  List<String> itemTypes,
                                  List<String> categories,
                                  List<String> tradeTypes) {

        String category  = post.getCategory()  == null ? "" : post.getCategory();
        String tradeType = post.getTradeType() == null ? "" : post.getTradeType();

        if (!itemTypes.isEmpty()) {
            boolean hit = false;
            for (String t : itemTypes) {
                if (category.startsWith(t)) { hit = true; break; }
            }
            if (!hit) return false;
        }

        if (!categories.isEmpty()) {
            boolean hit = false;
            for (String c : categories) {
                if (category.endsWith(c)) { hit = true; break; }
            }
            if (!hit) return false;
        }

        if (!tradeTypes.isEmpty()) {
            boolean hit = "BOTH".equals(tradeType);     // 직거래+택배 글은 양쪽 다 통과
            for (String t : tradeTypes) {
                if (tradeType.equals(t)) { hit = true; break; }
            }
            if (!hit) return false;
        }

        return true;
    }

    /** 글쓴이 + 댓글 작성자들의 닉네임을 한 번에 조회해서 Map 으로 만든다 */
    private Map<Long, String> buildNicknameMap(Post post, List<Comment> comments) {

        List<Long> userIds = new ArrayList<>();
        userIds.add(post.getUserId());

        for (Comment c : comments) {
            if (!userIds.contains(c.getUserId())) {
                userIds.add(c.getUserId());
            }
        }

        Map<Long, String> nicknames = new HashMap<>();

        for (Member m : memberRepository.findAllById(userIds)) {
            nicknames.put(m.getId(), m.getNICKNAME());
        }

        return nicknames;
    }

    /** 세션에서 로그인한 회원 번호를 꺼낸다 */
    private Long loginUserId(HttpSession session) {

        Long userId = (Long) session.getAttribute("loginUserId");

        if (userId == null) {
            userId = (Long) session.getAttribute("userID");
        }

        return userId;
    }

    /** 내가 관심 누른 글 번호 모음 */
    private Set<Long> myFavoriteIds(HttpSession session) {

        Set<Long> ids = new HashSet<>();

        Long userId = loginUserId(session);

        if (userId == null) {
            return ids;
        }

        for (PostFavorite f : postFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)) {
            ids.add(f.getPostId());
        }

        return ids;
    }
}