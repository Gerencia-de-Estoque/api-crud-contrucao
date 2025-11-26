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

import com.example.demo.api.dto.FerramentaDTO;
import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.service.FerramentaService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class FerramentaControllerTest {

    @Mock
    private FerramentaService ferramentaService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new FerramentaController(ferramentaService)).build();
    }

    @Test
    void deveListarFerramentas() throws Exception {
        FerramentaDTO dto = FerramentaDTO.builder()
                .codFerramenta(1)
                .codigoProduto("FER-001")
                .nome("Parafusadeira")
                .marca("Tools")
                .valor(99.9)
                .qtdPacote(2)
                .filial(FilialResumoDTO.builder().idFilial(7).nome("Matriz").build())
                .build();

        when(ferramentaService.listarTodos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/FERRAMENTA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Parafusadeira"))
                .andExpect(jsonPath("$[0].filial.idFilial").value(7));

        verify(ferramentaService).listarTodos();
    }

    @Test
    void deveBuscarPorIdRetornando404QuandoNaoEncontrado() throws Exception {
        when(ferramentaService.buscarPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/FERRAMENTA/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveCriarFerramentaRetornando201() throws Exception {
        FerramentaDTO semId = FerramentaDTO.builder()
                .codigoProduto("FER-010")
                .nome("Serrote")
                .marca("Max")
                .valor(49.9)
                .qtdPacote(1)
                .filial(FilialResumoDTO.builder().idFilial(3).build())
                .build();

        FerramentaDTO criado = FerramentaDTO.builder()
                .codFerramenta(10)
                .codigoProduto("FER-010")
                .nome("Serrote")
                .marca("Max")
                .valor(49.9)
                .qtdPacote(1)
                .filial(FilialResumoDTO.builder().idFilial(3).build())
                .build();

        when(ferramentaService.criar(any(FerramentaDTO.class))).thenReturn(criado);

        mockMvc.perform(post("/api/FERRAMENTA")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(semId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codFerramenta").value(10))
                .andExpect(jsonPath("$.nome").value("Serrote"));
    }

    @Test
    void deveDeletarRetornando204() throws Exception {
        when(ferramentaService.deletar(5)).thenReturn(true);

        mockMvc.perform(delete("/api/FERRAMENTA/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deveRetornar404AoDeletarInexistente() throws Exception {
        when(ferramentaService.deletar(6)).thenReturn(false);

        mockMvc.perform(delete("/api/FERRAMENTA/6"))
                .andExpect(status().isNotFound());
    }
}
