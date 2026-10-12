package com.ibom.main.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Post;

import jakarta.servlet.http.HttpSession;

@Controller
public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /** 나눔글 · 요청글 등록 (shareWrite.html / requestWrite.html 공용) */
    @PostMapping("/post/add")
    public String addPost(
            @RequestParam("type")                                String type,
            @RequestParam("title")                               String title,
            @RequestParam("content")                             String content,
            @RequestParam(value = "category",    required = false) String category,
            @RequestParam(value = "subCategory", required = false) String subCategory,
            @RequestParam(value = "urgency",     required = false) String urgency,
            @RequestParam(value = "tradeType",   required = false) String[] tradeType,
            HttpSession session) {

        /* 로그인 확인 (세션 키는 프로젝트에 따라 둘 중 하나) */
        Long userId = (Long) session.getAttribute("loginUserId");
        if (userId == null) {
            userId = (Long) session.getAttribute("userID");
        }
        if (userId == null) {
            return "redirect:/member/login";
        }

        boolean isRequest = "REQUEST".equals(type);

        /* 제목·내용 비어 있으면 되돌려보냄 */
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            return isRequest
                    ? "redirect:/board/requestWrite?error=empty"
                    : "redirect:/board/shareWrite?error=empty";
        }

        Post post = new Post();

        post.setUserId(userId);
        post.setType(isRequest ? "REQUEST" : "SHARE");
        post.setTitle(title.trim());
        post.setContent(content.trim());

        /* 카테고리 + 세부 카테고리를 한 칸에 저장 → "유아용품 > 수유" */
        if (category != null && !category.isBlank()) {
            if (subCategory != null && !subCategory.isBlank()) {
                post.setCategory(category + " > " + subCategory);
            } else {
                post.setCategory(category);
            }
        }

        /* 긴급도는 요청글에서만 의미 있음 */
        post.setUrgency("URGENT".equals(urgency) ? "URGENT" : "NORMAL");

        /* 거래방식 체크박스: 둘 다 체크 → BOTH */
        if (tradeType != null && tradeType.length > 0) {
            post.setTradeType(tradeType.length >= 2 ? "BOTH" : tradeType[0]);
        }

        post.setStatus("ACTIVE");

        /* 이 두 줄이 없으면 마이페이지 목록에서 글이 사라진다 */
        post.setIsDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        /* 저장 확인용: 내 활동 탭으로 이동 (2단계에서 목록 페이지로 변경) */
        return "redirect:/mypage/activity?tab=" + post.getType();
    }
}