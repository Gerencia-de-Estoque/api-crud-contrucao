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

import com.example.demo.api.dto.MaterialConstrucaoDTO;
import com.example.demo.api.service.MaterialConstrucaoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/MATERIAL-CONSTRUCAO")
@RequiredArgsConstructor
public class MaterialConstrucaoController {

    private final MaterialConstrucaoService materialConstrucaoService;

    @GetMapping
    public ResponseEntity<List<MaterialConstrucaoDTO>> listarTodos() {
        return ResponseEntity.ok(materialConstrucaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialConstrucaoDTO> buscarPorId(@PathVariable Integer id) {
        return materialConstrucaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MaterialConstrucaoDTO> criar(@RequestBody MaterialConstrucaoDTO dto) {
        MaterialConstrucaoDTO criado = materialConstrucaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialConstrucaoDTO> atualizar(@PathVariable Integer id,
                                                           @RequestBody MaterialConstrucaoDTO dto) {
        return materialConstrucaoService.atualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        boolean removido = materialConstrucaoService.deletar(id);
        if (removido) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
