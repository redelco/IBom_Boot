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

    /** 나눔 게시판 목록 */
    @GetMapping("/board/share")
    public String shareBoard(Model model, HttpSession session) {

        List<Post> posts =
                postRepository.findByTypeAndIsDeletedFalseOrderByCreatedAtDesc("SHARE");

        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("myFavorites", myFavoriteIds(session));

        return "board/share";
    }

    /** 나눔 상세 */
    @GetMapping("/board/shareDetail")
    public String shareDetail(@RequestParam("id") Long id,
                              Model model,
                              HttpSession session) {

        Post post = postRepository.findById(id).orElse(null);

        /* 없는 글이거나 삭제된 글이면 목록으로 */
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

    /* ===================== 아래는 도우미 메서드 ===================== */

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

    /** 세션에서 로그인한 회원 번호를 꺼낸다 (세션 키가 두 종류라 둘 다 확인) */
    private Long loginUserId(HttpSession session) {

        Long userId = (Long) session.getAttribute("loginUserId");

        if (userId == null) {
            userId = (Long) session.getAttribute("userID");
        }

        return userId;
    }

    /** 내가 관심 누른 글 번호 모음 (목록에서 하트 색을 칠하는 데 사용) */
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