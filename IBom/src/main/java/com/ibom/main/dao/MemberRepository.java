package com.ibom.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibom.main.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.LOGIN_ID = :loginId AND m.PASSWORD = :password")
    Optional<Member> login(@Param("loginId") String loginId, @Param("password") String password);
}