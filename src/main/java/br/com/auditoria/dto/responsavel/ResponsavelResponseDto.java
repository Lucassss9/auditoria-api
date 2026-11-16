package br.com.auditoria.dto.responsavel;


import br.com.auditoria.enums.FuncaoObra;

public record ResponsavelResponseDto(Long id, String obraId, String nome, FuncaoObra funcao, String email,
                                     boolean ativo, boolean receberEmail) {
}
