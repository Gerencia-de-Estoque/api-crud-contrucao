package com.example.demo.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.api.dto.FerramentaDTO;
import com.example.demo.api.service.FerramentaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/FERRAMENTA")
@RequiredArgsConstructor
public class FerramentaController {

    private final FerramentaService ferramentaService;

    @GetMapping
    public ResponseEntity<List<FerramentaDTO>> listarTodos() {
        return ResponseEntity.ok(ferramentaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FerramentaDTO> buscarPorId(@PathVariable Integer id) {
        return ferramentaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FerramentaDTO> criar(@RequestBody FerramentaDTO dto) {
        FerramentaDTO criado = ferramentaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FerramentaDTO> atualizar(@PathVariable Integer id,
                                                   @RequestBody FerramentaDTO dto) {
        return ferramentaService.atualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        boolean removido = ferramentaService.deletar(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
