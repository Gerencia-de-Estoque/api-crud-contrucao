package com.example.demo.api.dto;

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
public class MaterialConstrucaoDTO {
    private Integer codMaterial;
    private String codigoProduto;
    private Double valor;
    private String cor;
    private String nome;
    private String materiaPrima;

    private FilialResumoDTO filial; // referencia a filial (resumida)
}
