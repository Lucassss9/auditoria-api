package br.com.auditoria.dto.obra;

import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.Situacao;

public record ObraDto(String id, String codigo, String nome, Segmento segmento,
                      Situacao situacao, String filialId, FilialDto filial,
                      int totalAuditorias) {
}
