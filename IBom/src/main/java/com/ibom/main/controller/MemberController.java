package com.ibom.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.ibom.main.dao.MemberRepository;
import com.ibom.main.model.Member;

@Controller
public class MemberController {

	private final MemberRepository memberRepository;
	
	public MemberController(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}
	
	/*첫 화면*/
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	/*회원가입*/
	@GetMapping("/member/add")/*src/main/resources/templates/member/add.html 경로 나타냄*/
	public String addForm() {
		return "member/add";
	}
	
	/*회원가입 처리*/
	@PostMapping("/member/add")
	public String add(Member member) {
		memberRepository.save(member);
		
	return "redirect:/board/share";
	}
	
	/*회원가입의 '가입하기'누르면 나눔게시판으로 넘어가는 구간*/
	@GetMapping("/board/share")
	public String shareBoard() {
		
		return "board/share";
	}
	
	/*로그인 화면-> 로그인하면 나눔게시판으로 넘어감 */
	@GetMapping("/member/login")
	public String loginForm() {
		
		return "member/login";
	}
	
	@PostMapping("/member/login")
	public String main() {
		
		return "redirect:/board/share";
	}
}


