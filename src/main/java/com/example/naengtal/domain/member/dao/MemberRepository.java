package com.example.naengtal.domain.member.dao;

import com.example.naengtal.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

    List<Member> findByNameContainsOrIdContains(String name, String id);
}
