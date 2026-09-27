package com.ibom.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    /** TODO: 로그인 붙으면 임시값(1L) 제거 */
    private Long currentUserId(HttpSession session) {
        Object loginUserId = session.getAttribute("loginUserId");
        return (loginUserId != null) ? (Long) loginUserId : 1L;
    }
}