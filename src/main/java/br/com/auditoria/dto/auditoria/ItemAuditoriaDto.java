package br.com.auditoria.dto.auditoria;

import br.com.auditoria.enums.MotivoNaoContagem;
import br.com.auditoria.enums.SituacaoItem;

import java.math.BigDecimal;
import java.util.List;

public record ItemAuditoriaDto(String id, String codigo, String descricao,
                               String unidade, BigDecimal quantidadeSistema,
                               BigDecimal valorUnitarioReferencia, String lista,
                               BigDecimal quantidadeReal, SituacaoItem situacao,
                               MotivoNaoContagem motivoNaoContagem, String local, String observacao,
                               List<EvidenciaDto> evidencias) {
}
