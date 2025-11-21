package com.example.demo.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.dto.MaterialConstrucaoDTO;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.model.MaterialConstrucaoEntity;
import com.example.demo.api.repository.FilialRepository;
import com.example.demo.api.repository.MaterialConstrucaoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialConstrucaoService {

    private final MaterialConstrucaoRepository materialConstrucaoRepository;
    private final FilialRepository filialRepository;

    @Transactional(readOnly = true)
    public List<MaterialConstrucaoDTO> listarTodos() {
        return materialConstrucaoRepository.findAll()
                .stream()
                .map(this::mapearParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<MaterialConstrucaoDTO> buscarPorId(Integer id) {
        return materialConstrucaoRepository.findById(id)
                .map(this::mapearParaDTO);
    }

    @Transactional
    public MaterialConstrucaoDTO criar(MaterialConstrucaoDTO dto) {
        MaterialConstrucaoEntity entity = new MaterialConstrucaoEntity();
        atualizarCampos(entity, dto);
        MaterialConstrucaoEntity salvo = materialConstrucaoRepository.save(entity);
        return mapearParaDTO(salvo);
    }

    @Transactional
    public Optional<MaterialConstrucaoDTO> atualizar(Integer id, MaterialConstrucaoDTO dto) {
        return materialConstrucaoRepository.findById(id)
                .map(entity -> {
                    atualizarCampos(entity, dto);
                    MaterialConstrucaoEntity salvo = materialConstrucaoRepository.save(entity);
                    return mapearParaDTO(salvo);
                });
    }

    @Transactional
    public boolean deletar(Integer id) {
        if (!materialConstrucaoRepository.existsById(id)) {
            return false;
        }
        materialConstrucaoRepository.deleteById(id);
        return true;
    }

    private void atualizarCampos(MaterialConstrucaoEntity entity, MaterialConstrucaoDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados do material sao obrigatorios");
        }

        entity.setCodigoProduto(dto.getCodigoProduto());
        entity.setValor(dto.getValor());
        entity.setCor(dto.getCor());
        entity.setNome(dto.getNome());
        entity.setMateriaPrima(dto.getMateriaPrima());
        entity.setFilial(obterFilial(dto.getFilial()));
    }

    private FilialEntity obterFilial(FilialResumoDTO filialResumoDTO) {
        if (filialResumoDTO == null || filialResumoDTO.getIdFilial() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filial do material e obrigatoria");
        }

        return filialRepository.findById(filialResumoDTO.getIdFilial())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial nao encontrada"));
    }

    private MaterialConstrucaoDTO mapearParaDTO(MaterialConstrucaoEntity entity) {
        if (entity == null) {
            return null;
        }

        return MaterialConstrucaoDTO.builder()
                .codMaterial(entity.getCodMaterial())
                .codigoProduto(entity.getCodigoProduto())
                .valor(entity.getValor())
                .cor(entity.getCor())
                .nome(entity.getNome())
                .materiaPrima(entity.getMateriaPrima())
                .filial(mapearFilialResumo(entity.getFilial()))
                .build();
    }

    private FilialResumoDTO mapearFilialResumo(FilialEntity filial) {
        if (filial == null) {
            return null;
        }

        return FilialResumoDTO.builder()
                .idFilial(filial.getIdFilial())
                .nome(filial.getNome())
                .build();
    }
}
