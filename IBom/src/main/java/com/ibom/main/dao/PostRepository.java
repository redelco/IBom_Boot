package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /** 회원 탈퇴 시 그 회원의 글을 한꺼번에 가져옴 */
    List<Post> findByUserIdAndIsDeletedFalse(Long userId);

    /** 게시판 목록 (최신순) */
    List<Post> findByTypeAndIsDeletedFalseOrderByCreatedAtDesc(String type);

    /** 요청 게시판 - 긴급도 탭 */
    List<Post> findByTypeAndUrgencyAndIsDeletedFalseOrderByCreatedAtDesc(String type, String urgency);

    /** 메인 - 긴급 요청 3개 */
    List<Post> findTop3ByTypeAndUrgencyAndIsDeletedFalseOrderByCreatedAtDesc(String type, String urgency);

    /** 메인 - 최근 나눔 5개 (카드 그리드가 5칸) */
    List<Post> findTop5ByTypeAndIsDeletedFalseOrderByCreatedAtDesc(String type);

}