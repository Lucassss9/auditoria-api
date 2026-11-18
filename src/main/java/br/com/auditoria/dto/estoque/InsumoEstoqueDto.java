package br.com.auditoria.dto.estoque;

import java.math.BigDecimal;

public record InsumoEstoqueDto(String id,String codigo, String descricao,
                               BigDecimal quantidade, String unidade,
                               BigDecimal valorUnitario, String obraId, String obraNome) {
}
