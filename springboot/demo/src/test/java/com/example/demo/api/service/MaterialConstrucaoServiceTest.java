package com.example.demo.api.service;

import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.dto.MaterialConstrucaoDTO;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.model.MaterialConstrucaoEntity;
import com.example.demo.api.repository.FilialRepository;
import com.example.demo.api.repository.MaterialConstrucaoRepository;
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
class MaterialConstrucaoServiceTest {

    @Mock
    private MaterialConstrucaoRepository materialConstrucaoRepository;

    @Mock
    private FilialRepository filialRepository;

    private MaterialConstrucaoService service;

    @BeforeEach
    void setUp() {
        service = new MaterialConstrucaoService(materialConstrucaoRepository, filialRepository);
    }

    @Test
    @Disabled("Skipped by request")
    void listarTodosDeveMapearEntidadesParaDTOs() {
        FilialEntity filial = FilialEntity.builder()
                .idFilial(1)
                .nome("Matriz")
                .build();

        MaterialConstrucaoEntity entity = MaterialConstrucaoEntity.builder()
                .codMaterial(10)
                .codigoProduto("MAT-001")
                .valor(19.9)
                .cor("Azul")
                .nome("Tinta")
                .materiaPrima("Pigmento")
                .filial(filial)
                .build();

        when(materialConstrucaoRepository.findAll()).thenReturn(List.of(entity));

        List<MaterialConstrucaoDTO> resultado = service.listarTodos();

        assertThat(resultado)
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.getCodMaterial()).isEqualTo(10);
                    assertThat(dto.getCodigoProduto()).isEqualTo("MAT-001");
                    assertThat(dto.getFilial()).isNotNull();
                    assertThat(dto.getFilial().getNome()).isEqualTo("Matriz");
                });
    }

    @Test
    @Disabled("Skipped by request")
    void criarDevePersistirMaterialComFilialExistente() {
        FilialResumoDTO filialResumo = FilialResumoDTO.builder()
                .idFilial(7)
                .nome("Centro")
                .build();

        MaterialConstrucaoDTO dto = MaterialConstrucaoDTO.builder()
                .codigoProduto("MAT-002")
                .valor(49.9)
                .cor("Vermelho")
                .nome("Primer")
                .materiaPrima("Resina")
                .filial(filialResumo)
                .build();

        FilialEntity filialEntity = FilialEntity.builder()
                .idFilial(7)
                .nome("Centro")
                .build();

        when(filialRepository.findById(7)).thenReturn(Optional.of(filialEntity));
        when(materialConstrucaoRepository.save(any(MaterialConstrucaoEntity.class))).thenAnswer(invocation -> {
            MaterialConstrucaoEntity salvo = invocation.getArgument(0);
            salvo.setCodMaterial(22);
            return salvo;
        });

        MaterialConstrucaoDTO resultado = service.criar(dto);

        ArgumentCaptor<MaterialConstrucaoEntity> captor = ArgumentCaptor.forClass(MaterialConstrucaoEntity.class);
        verify(materialConstrucaoRepository).save(captor.capture());

        MaterialConstrucaoEntity salvo = captor.getValue();
        assertThat(salvo.getFilial()).isEqualTo(filialEntity);
        assertThat(resultado.getCodMaterial()).isEqualTo(22);
        assertThat(resultado.getFilial().getIdFilial()).isEqualTo(7);
    }
}
