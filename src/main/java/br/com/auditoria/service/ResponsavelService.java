package br.com.auditoria.service;

import br.com.auditoria.dto.responsavel.ResponsavelRequestDto;
import br.com.auditoria.dto.responsavel.ResponsavelResponseDto;
import br.com.auditoria.entity.Responsavel;
import br.com.auditoria.exception.ConflitoException;
import br.com.auditoria.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    @Transactional(readOnly = true)
    public List<ResponsavelResponseDto> listarTodos() {
        return responsavelRepository.findAll().stream().map(this::paraDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ResponsavelResponseDto> listarPorObra(String obraId) {
        return responsavelRepository.findByObraId(obraId).stream().map(this::paraDto).toList();
    }

    @Transactional
    public ResponsavelResponseDto criarResponsavel(ResponsavelRequestDto responsavelRequestDto) {
        exigirEmailInedito(responsavelRequestDto, null);

        Responsavel responsavel = new Responsavel();
        preencher(responsavelRequestDto, responsavel);
        Responsavel salvo = responsavelRepository.save(responsavel);

        return paraDto(salvo);
    }

    @Transactional
    public ResponsavelResponseDto atualizarResponsavel(ResponsavelRequestDto responsavelRequestDto, Long id) {
        Responsavel responsavel = responsavelRepository.findById(id).orElseThrow();
        exigirEmailInedito(responsavelRequestDto, id);
        preencher(responsavelRequestDto, responsavel);
        Responsavel salvo = responsavelRepository.save(responsavel);

        return paraDto(salvo);
    }

    @Transactional
    public void deletarResponsavel(Long id) {
        responsavelRepository.deleteById(id);
    }

    private void exigirEmailInedito(ResponsavelRequestDto requestDto, Long idEmEdicao) {
        responsavelRepository
                .findByEmailIgnoreCase(requestDto.email())
                .filter(existente -> !existente.getId().equals(idEmEdicao))
                .ifPresent(existente -> {
                    throw new ConflitoException(
                            "Esta pessoa ja esta cadastrada: " + requestDto.email());
                });
    }

    private ResponsavelResponseDto paraDto(Responsavel responsavel) {
        return new ResponsavelResponseDto(responsavel.getId(), List.copyOf(responsavel.getObraIds()),
                responsavel.getNome(), responsavel.getFuncaoObra(), responsavel.getEmail(),
                responsavel.isAtivo(), responsavel.isReceberEmail());
    }

    private void preencher(ResponsavelRequestDto responsavelRequestDto, Responsavel responsavel) {
        Set<String> obras = new LinkedHashSet<>(responsavelRequestDto.obraIds());

        responsavel.getObraIds().clear();
        responsavel.getObraIds().addAll(obras);
        responsavel.setNome(responsavelRequestDto.nome());
        responsavel.setFuncaoObra(responsavelRequestDto.funcao());
        responsavel.setEmail(responsavelRequestDto.email());
        responsavel.setAtivo(responsavelRequestDto.ativo());
        responsavel.setReceberEmail(responsavelRequestDto.receberEmail());
    }
}
