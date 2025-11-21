package com.example.demo.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_filial")
public class FilialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_filial", nullable = false, updatable = false,
            columnDefinition = "INT AUTO_INCREMENT")
    private Integer idFilial;

    @Column(name = "nome_filial", nullable = false, length = 150)
    private String nome;

    @Column(name = "login", nullable = false, length = 100, unique = true)
    private String login;

    @Column(name = "senha_hash", nullable = false, length = 255)
    private String senhaHash;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @JsonManagedReference("filial-ferramentas")
    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<FerramentaEntity> ferramentas;

    @JsonManagedReference("filial-materiais")
    @OneToMany(mappedBy = "filial", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<MaterialConstrucaoEntity> materiais;
}
