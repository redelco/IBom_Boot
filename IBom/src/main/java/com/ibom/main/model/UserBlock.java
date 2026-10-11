package com.ibom.main.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USER_BLOCKS")
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /** 차단한 사람 (= 나) */
    @Column(name = "BLOCKER_ID", nullable = false)
    private Long blockerId;

    /** 차단당한 사람 (= 상대) */
    @Column(name = "BLOCKED_ID", nullable = false)
    private Long blockedId;

    @Column(name = "CREATED_AT", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBlockerId() { return blockerId; }
    public void setBlockerId(Long blockerId) { this.blockerId = blockerId; }

    public Long getBlockedId() { return blockedId; }
    public void setBlockedId(Long blockedId) { this.blockedId = blockedId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}