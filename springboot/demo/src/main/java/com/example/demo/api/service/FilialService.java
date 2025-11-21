package com.example.demo.api.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.api.dto.FerramentaDTO;
import com.example.demo.api.dto.FilialDetalheDTO;
import com.example.demo.api.dto.FilialResumoDTO;
import com.example.demo.api.dto.MaterialConstrucaoDTO;
import com.example.demo.api.model.FerramentaEntity;
import com.example.demo.api.model.FilialEntity;
import com.example.demo.api.model.MaterialConstrucaoEntity;
import com.example.demo.api.repository.FilialRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FilialService {

    private final FilialRepository filialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<FilialResumoDTO> listarTodas() {
        return filialRepository.findAll()
                .stream()
                .map(this::mapearParaResumo)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<FilialDetalheDTO> buscarPorId(Integer id) {
        return filialRepository.findById(id)
                .map(this::mapearParaDetalhe);
    }

    @Transactional
    public FilialDetalheDTO criar(FilialResumoDTO dto) {
        FilialEntity entity = new FilialEntity();
        atualizarCampos(entity, dto);
        FilialEntity salvo = filialRepository.save(entity);
        return mapearParaDetalhe(salvo);
    }

    @Transactional
    public Optional<FilialDetalheDTO> atualizar(Integer id, FilialResumoDTO dto) {
        return filialRepository.findById(id)
                .map(entity -> {
                    atualizarCampos(entity, dto);
                    FilialEntity salvo = filialRepository.save(entity);
                    return mapearParaDetalhe(salvo);
                });
    }

    @Transactional
    public boolean deletar(Integer id) {
        if (!filialRepository.existsById(id)) {
            return false;
        }
        filialRepository.deleteById(id);
        return true;
    }

    private void atualizarCampos(FilialEntity entity, FilialResumoDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados da filial sao obrigatorios");
        }

        Integer dtoId = normalizarId(dto.getIdFilial());
        atualizarIdFilial(entity, dtoId);
        entity.setNome(validarNome(dto.getNome()));
        atualizarLogin(entity, dto.getLogin());
        atualizarSenha(entity, dto.getSenha());
        atualizarStatus(entity, dto.getAtivo());
    }

    private Integer normalizarId(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        return id;
    }

    private void atualizarIdFilial(FilialEntity entity, Integer dtoId) {
        if (entity.getIdFilial() == null) {
            if (dtoId != null) {
                entity.setIdFilial(dtoId);
            }
            return;
        }

        if (dtoId != null && !entity.getIdFilial().equals(dtoId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao e permitido alterar o codigo da filial");
        }
    }

    private String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da filial e obrigatorio");
        }
        return nome.trim();
    }

    private void atualizarLogin(FilialEntity entity, String login) {
        if (login == null || login.isBlank()) {
            if (entity.getLogin() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Login da filial e obrigatorio");
            }
            return;
        }
        entity.setLogin(login.trim());
    }

    private void atualizarSenha(FilialEntity entity, String senha) {
        if (entity.getSenhaHash() == null) {
            if (senha == null || senha.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha da filial e obrigatoria");
            }
            entity.setSenhaHash(passwordEncoder.encode(senha));
            return;
        }

        if (senha != null && !senha.isBlank()) {
            entity.setSenhaHash(passwordEncoder.encode(senha));
        }
    }

    private void atualizarStatus(FilialEntity entidade, Boolean ativo) {
        if (ativo != null) {
            entidade.setAtivo(ativo);
            return;
        }

        if (entidade.getAtivo() == null) {
            entidade.setAtivo(Boolean.TRUE);
        }
    }

    private FilialResumoDTO mapearParaResumo(FilialEntity entity) {
        if (entity == null) {
            return null;
        }

        return FilialResumoDTO.builder()
                .idFilial(entity.getIdFilial())
                .nome(entity.getNome())
                .login(entity.getLogin())
                .ativo(entity.getAtivo())
                .build();
    }

    private FilialDetalheDTO mapearParaDetalhe(FilialEntity entity) {
        if (entity == null) {
            return null;
        }

        List<FerramentaDTO> ferramentas = Optional.ofNullable(entity.getFerramentas())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapearFerramenta)
                .toList();

        List<MaterialConstrucaoDTO> materiais = Optional.ofNullable(entity.getMateriais())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::mapearMaterial)
                .toList();

        return FilialDetalheDTO.builder()
                .idFilial(entity.getIdFilial())
                .nome(entity.getNome())
                .ferramentas(ferramentas)
                .materiais(materiais)
                .build();
    }

    private FerramentaDTO mapearFerramenta(FerramentaEntity entity) {
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
                .filial(mapearParaResumo(entity.getFilial()))
                .build();
    }

    private MaterialConstrucaoDTO mapearMaterial(MaterialConstrucaoEntity entity) {
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
                .filial(mapearParaResumo(entity.getFilial()))
                .build();
    }
}
