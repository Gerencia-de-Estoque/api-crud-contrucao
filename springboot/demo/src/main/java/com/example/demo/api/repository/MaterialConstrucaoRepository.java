package com.example.demo.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.api.model.MaterialConstrucaoEntity;

public interface MaterialConstrucaoRepository extends JpaRepository<MaterialConstrucaoEntity, Integer> {
}
