package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ibom.main.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
