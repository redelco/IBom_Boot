package com.ibom.main.controller;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PostFavoriteRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Member;
import com.ibom.main.model.PostFavorite;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;
	private final PostFavoriteRepository postFavoriteRepository;

	public MemberController(MemberRepository memberRepository,
	                        PostRepository postRepository,
	                        PostFavoriteRepository postFavoriteRepository) {
		this.memberRepository = memberRepository;
		this.postRepository = postRepository;
		this.postFavoriteRepository = postFavoriteRepository;
	}

	/* 첫 화면 */
	@GetMapping("/")
	public String index(Model model, HttpSession session) {

		model.addAttribute("urgentPosts",
				postRepository.findTop3ByTypeAndUrgencyAndIsDeletedFalseOrderByCreatedAtDesc("REQUEST", "URGENT"));

		model.addAttribute("recentShares",
				postRepository.findTop5ByTypeAndIsDeletedFalseOrderByCreatedAtDesc("SHARE"));

		model.addAttribute("myFavorites", myFavoriteIds(session));

		return "index";
	}

	/* 회원가입 화면 */
	@GetMapping("/member/add")
	public String addForm() {
		return "member/add";
	}

	/* 회원가입 처리 */
	@PostMapping("/member/add")
	public String add(Member member) {

		/* 같은 아이디가 이미 있으면 저장하지 않는다 */
		if (memberRepository.existsLoginIdForSignup(member.getLOGIN_ID())) {
			return "redirect:/member/add?error=dup";
		}

		member.setIsActive(1);
		memberRepository.save(member);

		/* 가입 후 바로 로그인할 수 있게 로그인 화면으로 */
		return "redirect:/member/login";
	}

	/** 회원가입 - 아이디 중복 검사 (화면에서 fetch 로 호출) */
	@GetMapping("/member/check-login-id")
	@ResponseBody
	public String checkLoginId(@RequestParam("loginId") String loginId) {

		if (loginId == null || loginId.isBlank()) {
			return "EMPTY";
		}

		return memberRepository.existsLoginIdForSignup(loginId.trim()) ? "DUP" : "OK";
	}

	/** 회원가입 - 닉네임 중복 검사 */
	@GetMapping("/member/check-nickname")
	@ResponseBody
	public String checkNickname(@RequestParam("nickname") String nickname) {

		if (nickname == null || nickname.isBlank()) {
			return "EMPTY";
		}

		return memberRepository.existsNicknameForSignup(nickname.trim()) ? "DUP" : "OK";
	}

	/* 로그인 화면 */
	@GetMapping("/member/login")
	public String loginForm() {
		return "member/login";
	}

	/* 로그인 처리 */
	@PostMapping("/member/login")
	public String login(@RequestParam("LOGIN_ID") String loginId,
	                    @RequestParam("PASSWORD") String password,
	                    HttpSession session) {

		Member member = memberRepository.login(loginId, password).orElse(null);

		if (member == null) {
			return "redirect:/member/login?error";
		}

		/* 마이페이지·북마크·관심목록에서 사용 */
		session.setAttribute("loginUserId", member.getId());
		session.setAttribute("loginNickname", member.getNICKNAME());

		/* 댓글 기능이 사용하는 키 */
		session.setAttribute("userID", member.getId());
		session.setAttribute("login", true);

		return "redirect:/";
	}

	/* 로그아웃 */
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	/* ===================== 도우미 메서드 ===================== */

	private Long loginUserId(HttpSession session) {

		Long userId = (Long) session.getAttribute("loginUserId");

		if (userId == null) {
			userId = (Long) session.getAttribute("userID");
		}

		return userId;
	}

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