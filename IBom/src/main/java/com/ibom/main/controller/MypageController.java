package com.ibom.main.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.ibom.main.model.Post;
import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PolicyBookmarkRepository;
import com.ibom.main.dao.PostFavoriteRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final PolicyBookmarkRepository policyBookmarkRepository;

    public MypageController(MemberRepository memberRepository,
                            PostRepository postRepository,
                            PostFavoriteRepository postFavoriteRepository,
                            PolicyBookmarkRepository policyBookmarkRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postFavoriteRepository = postFavoriteRepository;
        this.policyBookmarkRepository = policyBookmarkRepository;
    }

    /** 마이페이지 홈 */
    @GetMapping("/mypage")
    public String home(HttpSession session, Model model) {

        Long userId = currentUserId(session);

        Member member = memberRepository.findById(userId).orElse(null);
        if (member == null) {
            return "redirect:/";
        }

        // 프로필 : 화면에서 쓸 값만 꺼내서 넘김
        model.addAttribute("nickname", member.getNICKNAME());
        model.addAttribute("joinedAt", member.getCreatedAt());

        // 통계
        model.addAttribute("shareCount",
                postRepository.countByUserIdAndTypeAndIsDeletedFalse(userId, "SHARE"));
        model.addAttribute("requestCount",
                postRepository.countByUserIdAndTypeAndIsDeletedFalse(userId, "REQUEST"));
        model.addAttribute("favoriteCount",
                postFavoriteRepository.countByUserId(userId)
                        + policyBookmarkRepository.countByUserId(userId));

        // 최근 작성 글
        model.addAttribute("sharePosts",
                postRepository.findTop4ByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(userId, "SHARE"));
        model.addAttribute("requestPosts",
                postRepository.findTop4ByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(userId, "REQUEST"));

        return "mypage/mypage";
    }

    /** 내 활동 (나눔내역 / 요청내역 / 관심목록) */
    @GetMapping("/mypage/activity")
    public String activity(@RequestParam(required = false, defaultValue = "SHARE") String tab,
                           @RequestParam(required = false, defaultValue = "latest") String sort,
                           HttpSession session, Model model) {

        Long userId = currentUserId(session);

        Sort sortOption = "oldest".equals(sort)
                ? Sort.by(Sort.Order.asc("createdAt"),  Sort.Order.asc("id"))
                : Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

        List<Post> posts = List.of();
        if ("SHARE".equals(tab) || "REQUEST".equals(tab)) {
            posts = postRepository.findByUserIdAndTypeAndIsDeletedFalse(userId, tab, sortOption);
        }

        model.addAttribute("tab", tab);
        model.addAttribute("sort", sort);
        model.addAttribute("posts", posts);
        model.addAttribute("pageDesc", pageDesc(tab));

        return "mypage/activity";
    }

    /** 내 글 삭제 : 실제로 지우지 않고 IS_DELETED 만 켬 */
    @PostMapping("/mypage/activity/delete")
    public String deletePost(@RequestParam Long id,
                             @RequestParam String tab,
                             HttpSession session) {

        Long userId = currentUserId(session);
        Post post = postRepository.findById(id).orElse(null);

        // 남의 글은 지울 수 없게 반드시 확인
        if (post != null && post.getUserId().equals(userId)) {
            post.setIsDeleted(true);
            postRepository.save(post);
        }

        return "redirect:/mypage/activity?tab=" + tab;
    }

    private String pageDesc(String tab) {
        if ("REQUEST".equals(tab))  return "내가 작성한 요청 글을 확인하고 관리할 수 있어요.";
        if ("FAVORITE".equals(tab)) return "관심을 누른 글이에요.";
        return "내가 작성한 나눔 글을 확인하고 관리할 수 있어요.";
    }

    /** TODO: 로그인 붙으면 임시값(1L) 제거 */
    private Long currentUserId(HttpSession session) {
        Object loginUserId = session.getAttribute("loginUserId");
        return (loginUserId != null) ? (Long) loginUserId : 1L;
    }
}