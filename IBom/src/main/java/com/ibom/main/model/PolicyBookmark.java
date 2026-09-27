package com.ibom.main.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** 육아소식 북마크 (POLICY_BOOKMARKS 테이블) */
@Getter
@Entity
@Table(name = "POLICY_BOOKMARKS")
public class PolicyBookmark {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Setter
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Setter
    @Column(name = "POLICY_ID", nullable = false)
    private Long policyId;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

}