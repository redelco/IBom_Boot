package com.ibom.main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.CommentRepository;
import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Comment;
import com.ibom.main.model.Member;
import com.ibom.main.model.Post;

@Controller
@RequestMapping("/request")
public class RequestController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public RequestController(PostRepository postRepository,
                             CommentRepository commentRepository,
                             MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    /** 요청 게시판 목록 */
    @GetMapping
    public String request(
            @RequestParam(value = "urgency", required = false) String urgency,
            Model model) {

        List<Post> posts;

        if ("URGENT".equals(urgency) || "NORMAL".equals(urgency)) {
            posts = postRepository
                    .findByTypeAndUrgencyAndIsDeletedFalseOrderByCreatedAtDesc("REQUEST", urgency);
        } else {
            urgency = "ALL";
            posts = postRepository
                    .findByTypeAndIsDeletedFalseOrderByCreatedAtDesc("REQUEST");
        }

        List<Long> userIds = new ArrayList<>();
        for (Post p : posts) {
            if (!userIds.contains(p.getUserId())) {
                userIds.add(p.getUserId());
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("nicknames", nicknamesOf(userIds));
        model.addAttribute("urgency", urgency);

        return "request/request";
    }

    /** 요청 상세 */
    @GetMapping("/requestdetail")
    public String requestDetail(@RequestParam("id") Long id, Model model) {

        Post post = postRepository.findById(id).orElse(null);

        if (post == null || Boolean.TRUE.equals(post.getIsDeleted())) {
            return "redirect:/request";
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id);

        List<Long> userIds = new ArrayList<>();
        userIds.add(post.getUserId());
        for (Comment c : comments) {
            if (!userIds.contains(c.getUserId())) {
                userIds.add(c.getUserId());
            }
        }

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("nicknames", nicknamesOf(userIds));

        return "request/requestdetail";
    }

    /** 회원 ID 목록 → 닉네임 Map (쿼리 한 번) */
    private Map<Long, String> nicknamesOf(List<Long> userIds) {

        Map<Long, String> map = new HashMap<>();

        if (userIds.isEmpty()) {
            return map;
        }

        for (Member m : memberRepository.findAllById(userIds)) {
            map.put(m.getId(), m.getNICKNAME());
        }

        return map;
    }
}