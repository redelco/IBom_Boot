package com.ibom.main.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibom.main.model.Member;
import com.ibom.main.model.Post;
import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PolicyBookmarkRepository;
import com.ibom.main.dao.PostFavoriteRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Member;
import com.ibom.main.dao.PolicyRepository;
import com.ibom.main.model.Policy;
import com.ibom.main.model.PolicyBookmark;
import com.ibom.main.model.PostFavorite;

import jakarta.servlet.http.HttpSession;

@Controller
public class MypageController {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final PolicyBookmarkRepository policyBookmarkRepository;
    private final PolicyRepository policyRepository;

    public MypageController(MemberRepository memberRepository,
                            PostRepository postRepository,
                            PostFavoriteRepository postFavoriteRepository,
                            PolicyBookmarkRepository policyBookmarkRepository, PolicyRepository policyRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
        this.postFavoriteRepository = postFavoriteRepository;
        this.policyBookmarkRepository = policyBookmarkRepository;
        this.policyRepository = policyRepository;
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
                           @RequestParam(required = false, defaultValue = "SHARE") String favType,
                           @RequestParam(required = false, defaultValue = "latest") String sort,
                           HttpSession session, Model model) {

        Long userId = currentUserId(session);

        Sort sortOption = "oldest".equals(sort)
                ? Sort.by(Sort.Order.asc("createdAt"),  Sort.Order.asc("id"))
                : Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));

        List<Post> posts = List.of();
        List<Policy> policies = List.of();

        if ("SHARE".equals(tab) || "REQUEST".equals(tab)) {

            /* 내가 쓴 글 */
            posts = postRepository.findByUserIdAndTypeAndIsDeletedFalse(userId, tab, sortOption);

        } else if ("FAVORITE".equals(tab)) {

            if ("NEWS".equals(favType)) {

                /* 북마크한 육아소식 */
                List<Long> policyIds = policyBookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId)
                        .stream().map(PolicyBookmark::getPolicyId).toList();

                if (!policyIds.isEmpty()) {
                    policies = policyRepository.findByIdInAndIsActiveTrue(policyIds);
                }

            } else {

                /* 관심 누른 나눔 · 요청 글 */
                List<Long> postIds = postFavoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                        .stream().map(PostFavorite::getPostId).toList();

                if (!postIds.isEmpty()) {
                    posts = postRepository.findByIdInAndTypeAndIsDeletedFalse(postIds, favType, sortOption);
                }
            }
        }

        model.addAttribute("tab", tab);
        model.addAttribute("favType", favType);
        model.addAttribute("sort", sort);
        model.addAttribute("posts", posts);
        model.addAttribute("policies", policies);
        model.addAttribute("pageDesc", pageDesc(tab));

        return "mypage/activity";
    }

    /** 관심 해제 */
    @PostMapping("/mypage/activity/favorite/remove")
    public String removeFavorite(@RequestParam Long id,
                                 @RequestParam String favType,
                                 HttpSession session) {

        Long userId = currentUserId(session);

        if ("NEWS".equals(favType)) {
            policyBookmarkRepository.deleteByUserIdAndPolicyId(userId, id);
        } else {
            postFavoriteRepository.deleteByUserIdAndPostId(userId, id);
        }

        return "redirect:/mypage/activity?tab=FAVORITE&favType=" + favType;
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

    /* =========================================================
   설정 - 화면
   ========================================================= */
    @GetMapping("/mypage/settings")
    public String settings(HttpSession session, Model model) {

        Long userId = currentUserId(session);
        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            return "redirect:/member/login";
        }

        model.addAttribute("nickname", member.getNICKNAME());
        model.addAttribute("loginId",  member.getLOGIN_ID());
        model.addAttribute("joinedAt", member.getCreatedAt());

        return "mypage/settings";
    }

    /* =========================================================
       설정 - 닉네임 수정 (POST 후 redirect)
       ========================================================= */
    @PostMapping("/mypage/settings/nickname")
    public String updateNickname(@RequestParam("nickname") String nickname,
                                 HttpSession session) {

        Long userId = currentUserId(session);
        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            return "redirect:/member/login";
        }

        String newNickname = (nickname == null) ? "" : nickname.trim();

        if (newNickname.isEmpty()) {
            return "redirect:/mypage/settings?error=empty";
        }
        if (newNickname.length() > 20) {
            return "redirect:/mypage/settings?error=length";
        }
        if (memberRepository.existsNicknameExceptMe(newNickname, userId)) {
            return "redirect:/mypage/settings?error=dup";
        }

        member.setNICKNAME(newNickname);
        memberRepository.save(member);

        /* 헤더에 보이는 닉네임도 같이 갱신 */
        session.setAttribute("loginNickname", newNickname);

        return "redirect:/mypage/settings?ok=1";
    }

    /* =========================================================
       설정 - 닉네임 중복 검색 버튼용 (문자열만 돌려줌)
       ========================================================= */
    @GetMapping("/mypage/settings/check-nickname")
    @ResponseBody
    public String checkNickname(@RequestParam("nickname") String nickname,
                                HttpSession session) {

        String value = (nickname == null) ? "" : nickname.trim();

        if (value.isEmpty()) {
            return "EMPTY";
        }

        return memberRepository.existsNicknameExceptMe(value, currentUserId(session))
                ? "DUP"
                : "OK";
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