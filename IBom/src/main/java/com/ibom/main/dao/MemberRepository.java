package com.ibom.main.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibom.main.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.LOGIN_ID = :loginId AND m.PASSWORD = :password")
    Optional<Member> login(@Param("loginId") String loginId, @Param("password") String password);

    /** 아이디 중복 검사 (회원가입용) */
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.LOGIN_ID = :loginId")
    boolean existsLoginId(@Param("loginId") String loginId);

    /** 닉네임 중복 검사 - 나 자신은 제외해야 '안 바꾸고 저장'도 통과함 */
    @Query("SELECT COUNT(m) > 0 FROM Member m WHERE m.NICKNAME = :nickname AND m.id <> :userId")
    boolean existsNicknameExceptMe(@Param("nickname") String nickname,
                                   @Param("userId") Long userId);
}