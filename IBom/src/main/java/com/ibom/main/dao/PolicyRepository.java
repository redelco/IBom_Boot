package com.ibom.main.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibom.main.model.Policy;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /** 이전글 : 지금 글보다 ID가 작은 것 중 가장 큰 것 */
    Optional<Policy> findFirstByIsActiveTrueAndIdLessThanOrderByIdDesc(Long id);

    /** 다음글 : 지금 글보다 ID가 큰 것 중 가장 작은 것 */
    Optional<Policy> findFirstByIsActiveTrueAndIdGreaterThanOrderByIdAsc(Long id);

    /**
     * 조건 검색.
     * 값이 빈 문자열이면 그 조건은 무시됩니다. (예: category 가 "" 이면 전체 카테고리)
     */
    @Query("SELECT p FROM Policy p "
            + "WHERE p.isActive = true "
            + "  AND (:category = '' OR p.category = :category) "
            + "  AND (:scope    = '' OR p.scope    = :scope) "
            + "  AND (:region   = '' OR p.region   = :region) "
            + "  AND (:keyword  = '' OR p.title   LIKE CONCAT('%', :keyword, '%') "
            + "                      OR p.summary LIKE CONCAT('%', :keyword, '%') "
            + "                      OR p.content LIKE CONCAT('%', :keyword, '%'))")
    List<Policy> search(@Param("category") String category,
                        @Param("scope") String scope,
                        @Param("region") String region,
                        @Param("keyword") String keyword,
                        Sort sort);
}