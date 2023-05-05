package com.example.naengtal.domain.fridge.dao;

import com.example.naengtal.domain.fridge.entity.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Integer> {
}
