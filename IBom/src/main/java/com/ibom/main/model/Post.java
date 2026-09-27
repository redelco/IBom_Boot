package com.ibom.main.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Setter;

/** 나눔 · 요청 게시글 (POSTS 테이블) */
@Entity
@Table(name = "POSTS")
public class Post {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Setter
    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    /** SHARE / REQUEST */
    @Setter
    @Column(name = "TYPE", nullable = false, length = 20)
    private String type;

    @Setter
    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;

    @Setter
    @Column(name = "CONTENT", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    @Column(name = "CATEGORY", length = 50)
    private String category;

    /** ACTIVE / COMPLETED / DELETED */
    @Setter
    @Column(name = "STATUS", length = 20)
    private String status;

    /** URGENT / NORMAL (요청글만 사용) */
    @Setter
    @Column(name = "URGENCY", length = 20)
    private String urgency;

    /** DIRECT / DELIVERY / BOTH */
    @Setter
    @Column(name = "TRADE_TYPE", length = 20)
    private String tradeType;

    @Setter
    @Column(name = "TRADE_REGION", length = 100)
    private String tradeRegion;

    @Setter
    @Column(name = "IS_DELETED")
    private Boolean isDeleted;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public String getType() { return type; }

    public String getTitle() { return title; }

    public String getContent() { return content; }

    public String getCategory() { return category; }

    public String getStatus() { return status; }

    public String getUrgency() { return urgency; }

    public String getTradeType() { return tradeType; }

    public String getTradeRegion() { return tradeRegion; }

    public Boolean getIsDeleted() { return isDeleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}