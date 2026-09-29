package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ibom.main.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    /** 마이페이지 홈 - 나눔/요청 개수 */
    long countByUserIdAndTypeAndIsDeletedFalse(Long userId, String type);

    /** 마이페이지 홈 - 최근 글 4개 */
    List<Post> findTop4ByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, String type);

    /** 내 활동 - 나눔내역 / 요청내역 (정렬 옵션) */
    List<Post> findByUserIdAndTypeAndIsDeletedFalse(Long userId, String type, Sort sort);

    /** 관심 누른 글 목록에서 쓰임 (ID 여러 개로 한 번에 조회) */
    List<Post> findByIdInAndTypeAndIsDeletedFalse(List<Long> ids, String type, Sort sort);
}