package com.example.demo.api.dto;

import java.util.List;

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
public class FilialDetalheDTO {
    private Integer idFilial;
    private String nome;

    private List<FerramentaDTO> ferramentas;
    private List<MaterialConstrucaoDTO> materiais;
}
