package com.example.naengtal.domain.ingredientalarm.dao;

import com.example.naengtal.domain.ingredientalarm.entity.IngredientAlarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientAlarmRepository extends JpaRepository<IngredientAlarm, Integer> {

}
