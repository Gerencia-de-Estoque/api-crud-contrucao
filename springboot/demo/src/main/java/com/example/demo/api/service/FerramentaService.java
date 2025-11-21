package com.example.demo.api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.api.dto.FerramentaDTO;
import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.model.FerramentaEntity;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.repository.FerramentaRepository;
import com.example.demo.api.repository.FilialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FerramentaService {

    private final FerramentaRepository ferramentaRepository;
    private final FilialRepository filialRepository;

    @Transactional(readOnly = true)
    public List<FerramentaDTO> listarTodos() {
        return ferramentaRepository.findAll()
                .stream()
                .map(this::mapearParaDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<FerramentaDTO> buscarPorId(Integer id) {
        return ferramentaRepository.findById(id)
                .map(this::mapearParaDTO);
    }

    @Transactional
    public FerramentaDTO criar(FerramentaDTO dto) {
        FerramentaEntity entity = new FerramentaEntity();
        atualizarCampos(entity, dto);
        FerramentaEntity salvo = ferramentaRepository.save(entity);
        return mapearParaDTO(salvo);
    }

    @Transactional
    public Optional<FerramentaDTO> atualizar(Integer id, FerramentaDTO dto) {
        return ferramentaRepository.findById(id)
                .map(entity -> {
                    atualizarCampos(entity, dto);
                    FerramentaEntity salvo = ferramentaRepository.save(entity);
                    return mapearParaDTO(salvo);
                });
    }

    @Transactional
    public boolean deletar(Integer id) {
        if (!ferramentaRepository.existsById(id)) {
            return false;
        }
        ferramentaRepository.deleteById(id);
        return true;
    }

    private void atualizarCampos(FerramentaEntity entity, FerramentaDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da ferramenta sao obrigatorios");
        }

        entity.setCodigoProduto(dto.getCodigoProduto());
        entity.setValor(dto.getValor());
        entity.setMarca(dto.getMarca());
        entity.setNome(dto.getNome());
        entity.setQtdPacote(dto.getQtdPacote());
        entity.setFilial(obterFilial(dto.getFilial()));
    }

    private FilialEntity obterFilial(FilialResumoDTO filialResumoDTO) {
        if (filialResumoDTO == null || filialResumoDTO.getIdFilial() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filial da ferramenta e obrigatoria");
        }

        return filialRepository.findById(filialResumoDTO.getIdFilial())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Filial nao encontrada"));
    }

    private FerramentaDTO mapearParaDTO(FerramentaEntity entity) {
        if (entity == null) {
            return null;
        }

        return FerramentaDTO.builder()
                .codFerramenta(entity.getCodFerramenta())
                .codigoProduto(entity.getCodigoProduto())
                .valor(entity.getValor())
                .marca(entity.getMarca())
                .nome(entity.getNome())
                .qtdPacote(entity.getQtdPacote())
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
