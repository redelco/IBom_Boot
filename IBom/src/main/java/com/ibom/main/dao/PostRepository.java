package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import com.ibom.main.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** 내가 쓴 글 개수 (type : SHARE 또는 REQUEST) */
    long countByUserIdAndTypeAndIsDeletedFalse(Long userId, String type);

    /** 내가 쓴 최근 글 4개 */
    List<Post> findTop4ByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, String type);

    /** 내가 쓴 글 전체 (정렬 방식은 호출할 때 정함) */
    List<Post> findByUserIdAndTypeAndIsDeletedFalse(Long userId, String type, Sort sort);
}