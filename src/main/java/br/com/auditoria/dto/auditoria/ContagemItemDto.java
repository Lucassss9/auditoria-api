package br.com.auditoria.dto.auditoria;

import br.com.auditoria.enums.MotivoNaoContagem;
import br.com.auditoria.enums.SituacaoItem;

import java.math.BigDecimal;

public record ContagemItemDto(BigDecimal quantidadeReal, SituacaoItem situacao,
                              MotivoNaoContagem motivoNaoContagem, String local,
                              String observacao) {
}
