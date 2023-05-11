package com.example.naengtal.domain.ingredientalarm.dao;

import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import com.example.naengtal.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientAlarmRepository extends JpaRepository<IngredientAlarm, Integer> {

    List<IngredientAlarm> findByMemberOrderByCreatedAtDesc(Member member);
}
