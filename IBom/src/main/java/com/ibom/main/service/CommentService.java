package com.ibom.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ibom.main.dao.CommentRepository;
import com.ibom.main.model.Comment;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    private CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /*댓글등록*/
    public Comment addComment(Comment comment) {

        return commentRepository.save(comment);
    }

    /*특정 게시글의 댓글 조회*/
    public List<Comment> getCommentsByPostId(Long postId){

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    /* 댓글 수정 */
    public Comment updateComment(Long id, String content) {

        Comment comment = commentRepository.findById(id).orElse(null);

        if (comment != null) {
            comment.setContent(content);

            return commentRepository.save(comment);
        }

        return null;
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
