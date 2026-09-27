package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ibom.main.model.PostFavorite;

public interface PostFavoriteRepository extends JpaRepository<PostFavorite, Long> {

    /** 내가 관심 누른 개수 */
    long countByUserId(Long userId);



    /** 내가 관심 누른 글 목록 (관심목록 화면에서 사용 예정) */
    List<PostFavorite> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Transactional
    void deleteByUserIdAndPostId(Long userId, Long postId);


}