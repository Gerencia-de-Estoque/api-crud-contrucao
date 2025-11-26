package com.example.demo.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.api.dto.FilialDetalheDTO;
import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.repository.FilialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FilialServiceTest {

    @Mock
    private FilialRepository filialRepository;

@Mock
private PasswordEncoder passwordEncoder;

@InjectMocks
private FilialService filialService;

// Testes unitarios de servico (sem subir contexto Spring)
@Test
void criarFilialSalvaComSenhaCodificadaEAtivoDefault() {
        FilialResumoDTO dto = FilialResumoDTO.builder()
                .nome("Loja Central")
                .login("central")
                .senha("segredo")
                .build();

        when(passwordEncoder.encode("segredo")).thenReturn("hash-segredo");
        when(filialRepository.save(any(FilialEntity.class))).thenAnswer(invocation -> {
            FilialEntity entity = invocation.getArgument(0);
            entity.setIdFilial(10);
            return entity;
        });

        FilialDetalheDTO resultado = filialService.criar(dto);

        ArgumentCaptor<FilialEntity> captor = ArgumentCaptor.forClass(FilialEntity.class);
        verify(filialRepository).save(captor.capture());
        FilialEntity salvo = captor.getValue();

        assertEquals("Loja Central", salvo.getNome());
        assertEquals("central", salvo.getLogin());
        assertEquals("hash-segredo", salvo.getSenhaHash());
        assertEquals(Boolean.TRUE, salvo.getAtivo());
        assertNotNull(resultado);
        assertEquals(10, resultado.getIdFilial());
        assertEquals("Loja Central", resultado.getNome());
        assertTrue(resultado.getFerramentas().isEmpty());
        assertTrue(resultado.getMateriais().isEmpty());
    }

    @Test
    void criarFilialSemNomeLancaBadRequest() {
        FilialResumoDTO dto = FilialResumoDTO.builder()
                .login("usuario")
                .senha("123")
                .build();

        ResponseStatusException excecao = assertThrows(ResponseStatusException.class, () -> filialService.criar(dto));

        assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
        verify(filialRepository, never()).save(any());
    }

// Teste parametrizado (nomes invalidos)
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "   "})
void criarFilialComNomeInvalidoLancaBadRequest(String nomeInvalido) {
        FilialResumoDTO dto = FilialResumoDTO.builder()
                .nome(nomeInvalido)
                .login("usuario")
                .senha("123")
                .build();

        ResponseStatusException excecao = assertThrows(ResponseStatusException.class, () -> filialService.criar(dto));

        assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
        verify(filialRepository, never()).save(any());
    }

// Teste parametrizado (logins invalidos)
@ParameterizedTest
@NullAndEmptySource
@ValueSource(strings = {" ", "   "})
void criarFilialComLoginInvalidoLancaBadRequest(String loginInvalido) {
        FilialResumoDTO dto = FilialResumoDTO.builder()
                .nome("Filial X")
                .login(loginInvalido)
                .senha("123")
                .build();

        ResponseStatusException excecao = assertThrows(ResponseStatusException.class, () -> filialService.criar(dto));

        assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
        verify(filialRepository, never()).save(any());
    }

    @Test
    void atualizarFilialMantemSenhaQuandoNaoInformada() {
        FilialEntity existente = FilialEntity.builder()
                .idFilial(5)
                .nome("Original")
                .login("original")
                .senhaHash("hash-antigo")
                .ativo(Boolean.TRUE)
                .build();

        FilialResumoDTO dto = FilialResumoDTO.builder()
                .idFilial(5)
                .nome("Atualizada")
                .login("novo-login")
                .build();

        when(filialRepository.findById(5)).thenReturn(Optional.of(existente));
        when(filialRepository.save(any(FilialEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FilialDetalheDTO resultado = filialService.atualizar(5, dto).orElseThrow();

        assertEquals(5, resultado.getIdFilial());
        assertEquals("Atualizada", resultado.getNome());

        ArgumentCaptor<FilialEntity> captor = ArgumentCaptor.forClass(FilialEntity.class);
        verify(filialRepository).save(captor.capture());
        FilialEntity salvo = captor.getValue();
        assertEquals("hash-antigo", salvo.getSenhaHash());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void atualizarComIdDiferenteRecusaAlteracao() {
        FilialEntity existente = FilialEntity.builder()
                .idFilial(5)
                .nome("Original")
                .login("original")
                .senhaHash("hash")
                .ativo(Boolean.TRUE)
                .build();

        FilialResumoDTO dto = FilialResumoDTO.builder()
                .idFilial(7)
                .nome("Alterada")
                .login("novo")
                .build();

        when(filialRepository.findById(5)).thenReturn(Optional.of(existente));

        ResponseStatusException excecao = assertThrows(ResponseStatusException.class,
                () -> filialService.atualizar(5, dto));

        assertEquals(HttpStatus.BAD_REQUEST, excecao.getStatusCode());
        verify(filialRepository, never()).save(any());
    }
}
