package br.com.auditoria.dto.cfobras;

import java.math.BigDecimal;

public record LinhaEstoqueCfDto(InsumoCfDto insumo, ObraCfDto obra, BigDecimal qtde, String valor_unitario) {
}
