package com.example.demo.api.service;

import com.example.demo.api.dto.FerramentaDTO;
import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.model.FerramentaEntity;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.repository.FerramentaRepository;
import com.example.demo.api.repository.FilialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FerramentaServiceTest {

    @Mock
    private FerramentaRepository ferramentaRepository;

    @Mock
    private FilialRepository filialRepository;

    private FerramentaService service;

    @BeforeEach
    void setUp() {
        service = new FerramentaService(ferramentaRepository, filialRepository);
    }

    @Test
    @Disabled("Skipped by request")
    void listarTodosDeveRetornarFerramentasMapeadas() {
        FilialEntity filial = FilialEntity.builder()
                .idFilial(2)
                .nome("Unidade Sul")
                .build();

        FerramentaEntity entity = FerramentaEntity.builder()
                .codFerramenta(30)
                .codigoProduto("FER-001")
                .valor(129.9)
                .marca("Tools")
                .nome("Parafusadeira")
                .qtdPacote(2)
                .filial(filial)
                .build();

        when(ferramentaRepository.findAll()).thenReturn(List.of(entity));

        List<FerramentaDTO> resultado = service.listarTodos();

        assertThat(resultado)
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.getCodFerramenta()).isEqualTo(30);
                    assertThat(dto.getFilial()).isNotNull();
                    assertThat(dto.getFilial().getNome()).isEqualTo("Unidade Sul");
                });
    }

    @Test
    @Disabled("Skipped by request")
    void criarDeveSalvarFerramentaComFilialValida() {
        FilialResumoDTO filialResumo = FilialResumoDTO.builder()
                .idFilial(9)
                .nome("Unidade Leste")
                .build();

        FerramentaDTO dto = FerramentaDTO.builder()
                .codigoProduto("FER-020")
                .valor(59.9)
                .marca("Max")
                .nome("Martelo")
                .qtdPacote(4)
                .filial(filialResumo)
                .build();

        FilialEntity filialEntity = FilialEntity.builder()
                .idFilial(9)
                .nome("Unidade Leste")
                .build();

        when(filialRepository.findById(9)).thenReturn(Optional.of(filialEntity));
        when(ferramentaRepository.save(any(FerramentaEntity.class))).thenAnswer(invocation -> {
            FerramentaEntity salvo = invocation.getArgument(0);
            salvo.setCodFerramenta(41);
            return salvo;
        });

        FerramentaDTO resultado = service.criar(dto);

        ArgumentCaptor<FerramentaEntity> captor = ArgumentCaptor.forClass(FerramentaEntity.class);
        verify(ferramentaRepository).save(captor.capture());

        FerramentaEntity salvo = captor.getValue();
        assertThat(salvo.getFilial()).isEqualTo(filialEntity);
        assertThat(resultado.getCodFerramenta()).isEqualTo(41);
        assertThat(resultado.getFilial().getIdFilial()).isEqualTo(9);
    }
}
