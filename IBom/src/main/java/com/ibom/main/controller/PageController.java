package com.ibom.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 화면 확인용 임시 Controller.
 * 기능을 붙이면서 하나씩 실제 Controller로 옮기고, 여기서는 지웁니다.
 * ("/", "/member/add", "/board/share" 는 MemberController 에 이미 있으므로 넣지 않습니다)
 */
@Controller
public class PageController {

	/* ===== 게시판 ===== */
	@GetMapping("/board/shareDetail")
	public String shareDetail() {
		return "board/shareDetail";
	}

	@GetMapping("/board/shareWrite")
	public String shareWrite() {
		return "board/shareWrite";
	}

	@GetMapping("/board/requestWrite")
	public String requestWrite() {
		return "board/requestWrite";
	}

	/* ===== 마이페이지 ===== */

	@GetMapping("/mypage/settings")
	public String settings() {
		return "mypage/settings";
	}
}