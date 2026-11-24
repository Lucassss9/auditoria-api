package br.com.auditoria.dto.responsavel;


import br.com.auditoria.enums.FuncaoObra;

import java.util.List;

public record ResponsavelResponseDto(Long id, List<String> obraIds, String nome, FuncaoObra funcao, String email,
                                     boolean ativo, boolean receberEmail) {
}
