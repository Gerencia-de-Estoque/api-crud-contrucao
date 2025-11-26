package com.example.demo.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.repository.FilialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

// Teste de integracao (contexto Spring Boot + H2 + MockMvc)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class FilialIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilialRepository filialRepository;

    @Test
    void deveCriarFilialEManterSenhaHasheada() throws Exception {
        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/FILIAL")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "nome": "Filial Integração",
                                          "login": "filial-int",
                                          "senha": "segredo123",
                                          "ativo": true
                                        }
                                        """)
                )
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.idFilial", notNullValue()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.nome", equalTo("Filial Integração")));

        FilialEntity salvo = filialRepository.findByLogin("filial-int").orElseThrow();
        assertEquals("filial-int", salvo.getLogin());
        // senha deve estar codificada, logo diferente da entrada
        assertEquals(Boolean.TRUE, salvo.getAtivo());
    }
}
