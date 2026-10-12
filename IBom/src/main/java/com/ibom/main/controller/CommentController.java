package com.ibom.main.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.ibom.main.dao.CommentRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Comment;
import com.ibom.main.model.Post;

import jakarta.servlet.http.HttpSession;

@Controller
public class CommentController {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentController(CommentRepository commentRepository,
                             PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    /** 댓글 등록 (나눔 상세 / 요청 상세 공용) */
    @PostMapping("/comment/add")
    public String addComment(Comment comment, HttpSession session) {

        Long userID = (Long) session.getAttribute("userID");
        if (userID == null) {
            userID = (Long) session.getAttribute("loginUserId");
        }
        if (userID == null) {
            return "redirect:/member/login";
        }

        Long postID = comment.getPostId();
        Post post = postRepository.findById(postID).orElse(null);

        if (post == null) {
            return "redirect:/";
        }

        /* 글 종류에 따라 돌아갈 페이지를 정한다 */
        String backUrl = "SHARE".equals(post.getType())
                ? "redirect:/board/shareDetail?id=" + postID
                : "redirect:/request/requestdetail?id=" + postID;

        /* 빈 댓글은 저장하지 않는다 (브라우저 required 는 F12로 지울 수 있음) */
        if (comment.getContent() == null || comment.getContent().isBlank()) {
            return backUrl;
        }

        comment.setUserId(userID);
        comment.setContent(comment.getContent().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        commentRepository.save(comment);

        return backUrl;
    }
}