package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ibom.main.model.PolicyBookmark;

public interface PolicyBookmarkRepository extends JpaRepository<PolicyBookmark, Long> {

    /** 내가 저장한 육아소식 개수 */
    long countByUserId(Long userId);

    /** 이 사람이 이 정책을 저장해뒀는지 */
    boolean existsByUserIdAndPolicyId(Long userId, Long policyId);

    /** 북마크 해제 (삭제는 트랜잭션이 필요함) */
    @Transactional
    void deleteByUserIdAndPolicyId(Long userId, Long policyId);

    /** 내가 저장한 육아소식 (마이페이지 관심목록에서 사용 예정) */
    List<PolicyBookmark> findByUserIdOrderByCreatedAtDesc(Long userId);
}