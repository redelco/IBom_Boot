package com.ibom.main.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ibom.main.dao.CommentRepository;
import com.ibom.main.dao.MemberRepository;
import com.ibom.main.dao.PostRepository;
import com.ibom.main.model.Comment;
import com.ibom.main.model.Member;
import com.ibom.main.model.Post;

@Controller
public class BoardController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    public BoardController(PostRepository postRepository,
                           CommentRepository commentRepository,
                           MemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.memberRepository = memberRepository;
    }

    /** 나눔 게시판 목록 */
    @GetMapping("/board/share")
    public String shareBoard(Model model) {

        List<Post> posts =
                postRepository.findByTypeAndIsDeletedFalseOrderByCreatedAtDesc("SHARE");

        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());

        return "board/share";
    }

    /** 나눔 상세 */
    @GetMapping("/board/shareDetail")
    public String shareDetail(@RequestParam("id") Long id, Model model) {

        Post post = postRepository.findById(id).orElse(null);

        /* 없는 글이거나 삭제된 글이면 목록으로 */
        if (post == null || Boolean.TRUE.equals(post.getIsDeleted())) {
            return "redirect:/board/share";
        }

        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtAsc(id);

        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("nicknames", buildNicknameMap(post, comments));

        return "board/shareDetail";
    }

    /**
     * 글쓴이 + 댓글 작성자들의 닉네임을 한 번에 조회해서 Map 으로 만든다.
     * (댓글마다 조회하면 쿼리가 댓글 수만큼 나가므로 한 번에 가져온다)
     */
    private Map<Long, String> buildNicknameMap(Post post, List<Comment> comments) {

        List<Long> userIds = new ArrayList<>();
        userIds.add(post.getUserId());

        for (Comment c : comments) {
            if (!userIds.contains(c.getUserId())) {
                userIds.add(c.getUserId());
            }
        }

        Map<Long, String> nicknames = new HashMap<>();

        for (Member m : memberRepository.findAllById(userIds)) {
            nicknames.put(m.getId(), m.getNICKNAME());
        }

        return nicknames;
    }
}