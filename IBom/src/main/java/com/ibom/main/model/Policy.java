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

/** 육아소식 (POLICIES 테이블) */
@Getter
@Entity
@Table(name = "POLICIES")
public class Policy {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Setter
    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Setter
    @Column(name = "CATEGORY", length = 50)
    private String category;

    @Setter
    @Column(name = "SUMMARY", length = 300)
    private String summary;

    @Setter
    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    @Column(name = "SCOPE", length = 20, nullable = false)
    private String scope;

    @Setter
    @Column(name = "REGION", length = 100)
    private String region;

    @Setter
    @Column(name = "SOURCE_URL", length = 500)
    private String sourceUrl;

    @Setter
    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

}