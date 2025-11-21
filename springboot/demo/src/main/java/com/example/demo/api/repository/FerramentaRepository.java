package com.example.demo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.api.model.FerramentaEntity;

public interface FerramentaRepository extends JpaRepository<FerramentaEntity, Integer> {
}
