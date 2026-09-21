package com.ibom.main.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ibom.main.model.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
