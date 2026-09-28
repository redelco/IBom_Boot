package com.ibom.main.controller;

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

	public MemberController(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	/* 첫 화면 */
	@GetMapping("/")
	public String index() {
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

	/* 나눔 게시판 */
	@GetMapping("/board/share")
	public String shareBoard() {
		return "board/share";
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

		/* 이 두 줄이 마이페이지·북마크·관심목록과 연결됩니다 */
		session.setAttribute("loginUserId", member.getId());
		session.setAttribute("loginNickname", member.getNICKNAME());

		return "redirect:/";
	}

	/* 로그아웃 */
	@GetMapping("/member/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
}