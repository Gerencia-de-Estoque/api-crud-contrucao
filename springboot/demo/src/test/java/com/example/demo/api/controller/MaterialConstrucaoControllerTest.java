package com.example.demo.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.dto.MaterialConstrucaoDTO;
import com.example.demo.api.service.MaterialConstrucaoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class MaterialConstrucaoControllerTest {

    @Mock
    private MaterialConstrucaoService materialConstrucaoService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MaterialConstrucaoController(materialConstrucaoService)).build();
    }

    @Test
    void deveListarMateriais() throws Exception {
        MaterialConstrucaoDTO dto = MaterialConstrucaoDTO.builder()
                .codMaterial(2)
                .codigoProduto("MAT-001")
                .nome("Cimento")
                .valor(35.0)
                .cor("Cinza")
                .materiaPrima("Pozolana")
                .filial(FilialResumoDTO.builder().idFilial(8).nome("Leste").build())
                .build();

        when(materialConstrucaoService.listarTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/MATERIAL-CONSTRUCAO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Cimento"))
                .andExpect(jsonPath("$[0].filial.idFilial").value(8));

        verify(materialConstrucaoService).listarTodos();
    }

    @Test
    void deveBuscarPorIdRetornando404QuandoNaoExistir() throws Exception {
        when(materialConstrucaoService.buscarPorId(77)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/MATERIAL-CONSTRUCAO/77"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriarMaterialRetornando201() throws Exception {
        MaterialConstrucaoDTO semId = MaterialConstrucaoDTO.builder()
                .codigoProduto("MAT-050")
                .nome("Areia")
                .valor(15.0)
                .cor("Amarela")
                .materiaPrima("Quartzo")
                .filial(FilialResumoDTO.builder().idFilial(2).build())
                .build();

        MaterialConstrucaoDTO criado = MaterialConstrucaoDTO.builder()
                .codMaterial(50)
                .codigoProduto("MAT-050")
                .nome("Areia")
                .valor(15.0)
                .cor("Amarela")
                .materiaPrima("Quartzo")
                .filial(FilialResumoDTO.builder().idFilial(2).build())
                .build();

        when(materialConstrucaoService.criar(any(MaterialConstrucaoDTO.class))).thenReturn(criado);

        mockMvc.perform(post("/api/MATERIAL-CONSTRUCAO")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(semId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codMaterial").value(50))
                .andExpect(jsonPath("$.nome").value("Areia"));
    }

    @Test
    void deveDeletarRetornando204() throws Exception {
        when(materialConstrucaoService.deletar(11)).thenReturn(true);

        mockMvc.perform(delete("/api/MATERIAL-CONSTRUCAO/11"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoDeletarInexistente() throws Exception {
        when(materialConstrucaoService.deletar(12)).thenReturn(false);

        mockMvc.perform(delete("/api/MATERIAL-CONSTRUCAO/12"))
                .andExpect(status().isNotFound());
    }
}
