package com.ibom.main.controller;


import org.springframework.ui.Model;
import com.ibom.main.dao.PostRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.MemberRepository;
import com.ibom.main.model.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {

	private final MemberRepository memberRepository;
	private final PostRepository postRepository;          // ← 추가

	public MemberController(MemberRepository memberRepository,
	                        PostRepository postRepository) {   // ← 추가
		this.memberRepository = memberRepository;
		this.postRepository = postRepository;             // ← 추가
	}

	/* 첫 화면 */
	@GetMapping("/")
	public String index(Model model) {

		model.addAttribute("urgentPosts",
				postRepository.findTop3ByTypeAndUrgencyAndIsDeletedFalseOrderByCreatedAtDesc("REQUEST", "URGENT"));

		model.addAttribute("recentShares",
				postRepository.findTop4ByTypeAndIsDeletedFalseOrderByCreatedAtDesc("SHARE"));

		return "index";
	}

	/* 회원가입 */
	@GetMapping("/member/add")
	public String addForm() {
		return "member/add";
	}

	@PostMapping("/member/add")
	public String add(Member member) {
		memberRepository.save(member);
		return "redirect:/";
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

		/* 댓글 기능(CommentController)이 사용하는 키 — 없으면 댓글 등록이 로그인 화면으로 튕깁니다 */
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
}