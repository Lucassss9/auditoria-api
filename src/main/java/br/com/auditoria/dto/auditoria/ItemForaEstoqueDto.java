package br.com.auditoria.dto.auditoria;

import java.math.BigDecimal;

public record ItemForaEstoqueDto(String id, String descricao, String codigo, String unidade,
                                 BigDecimal quantidade, BigDecimal valorUnitario, String local,
                                 String observacao) {
}
