package com.example.demo.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.api.model.FilialEntity;

public interface FilialRepository extends JpaRepository<FilialEntity, Integer> {

    Optional<FilialEntity> findByLogin(String login);
}
