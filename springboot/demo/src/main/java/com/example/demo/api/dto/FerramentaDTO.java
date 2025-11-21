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
public class FerramentaDTO {
    private Integer codFerramenta;
    private String codigoProduto;
    private Double valor;
    private String marca;
    private String nome;
    private Integer qtdPacote;

    private FilialResumoDTO filial; // referencia a filial (resumida)
}
