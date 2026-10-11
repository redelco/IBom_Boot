package com.ibom.main.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ibom.main.model.UserBlock;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

    /** 내가 차단한 목록 (최근 차단한 순) */
    List<UserBlock> findByBlockerIdOrderByCreatedAtDesc(Long blockerId);

    /** 이미 차단했는지 */
    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    /** 차단 해제 */
    @Transactional
    void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}