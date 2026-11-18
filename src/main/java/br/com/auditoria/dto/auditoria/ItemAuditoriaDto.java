package br.com.auditoria.dto.auditoria;

import java.math.BigDecimal;
import java.util.List;

public record ItemAuditoriaDto(String id, String codigo, String descricao, String categoria,
                               String unidade, BigDecimal quantidadeSistema,
                               BigDecimal valorUnitarioReferencia, String lista,
                               BigDecimal quantidadeReal, String situacao,
                               String motivoNaoContagem, String local, String observacao,
                               List<Object> evidencias) {
}
