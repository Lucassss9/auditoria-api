package br.com.auditoria.service;

import br.com.auditoria.dto.responsavel.ResponsavelRequestDto;
import br.com.auditoria.dto.responsavel.ResponsavelResponseDto;
import br.com.auditoria.entity.Responsavel;
import br.com.auditoria.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    public List<ResponsavelResponseDto> listarTodos() {
        return responsavelRepository.findAll().stream().map(this::paraDto).toList();
    }

    public List<ResponsavelResponseDto> listarPorObra(String obraId) {
        return responsavelRepository.findByObraId(obraId).stream().map(this::paraDto).toList();
    }

    public ResponsavelResponseDto criarResponsavel(ResponsavelRequestDto responsavelRequestDto) {
        Responsavel responsavel = new Responsavel();
        preencher(responsavelRequestDto, responsavel);
        Responsavel salvo = responsavelRepository.save(responsavel);

        return paraDto(salvo);
    }

    public ResponsavelResponseDto atualizarResponsavel(ResponsavelRequestDto responsavelRequestDto, Long id) {
        Responsavel responsavel = responsavelRepository.findById(id).orElseThrow();
        preencher(responsavelRequestDto, responsavel);
        Responsavel salvo = responsavelRepository.save(responsavel);

        return paraDto(salvo);
    }

    public void deletarResponsavel(Long id) {
        responsavelRepository.deleteById(id);
    }

    private ResponsavelResponseDto paraDto(Responsavel responsavel) {
        return new ResponsavelResponseDto(responsavel.getId(), responsavel.getObraId(), responsavel.getNome(),
                responsavel.getFuncaoObra(), responsavel.getEmail(), responsavel.isAtivo(), responsavel.isReceberEmail());
    }

    private void preencher(ResponsavelRequestDto responsavelRequestDto, Responsavel responsavel) {
        responsavel.setObraId(responsavelRequestDto.obraId());
        responsavel.setNome(responsavelRequestDto.nome());
        responsavel.setFuncaoObra(responsavelRequestDto.funcao());
        responsavel.setEmail(responsavelRequestDto.email());
        responsavel.setAtivo(responsavelRequestDto.ativo());
        responsavel.setReceberEmail(responsavelRequestDto.receberEmail());
    }
}
