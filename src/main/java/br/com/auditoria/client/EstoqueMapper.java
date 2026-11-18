package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class EstoqueMapper {

    public static InsumoEstoqueDto mapear(LinhaEstoqueCfDto linha) {
        return new InsumoEstoqueDto(linha.insumo()._id(), linha.insumo().codigo(), linha.insumo().descricao(),
                linha.qtde().setScale(3, RoundingMode.HALF_UP), linha.insumo().unidade(), paraValor(linha.valor_unitario()), linha.obra()._id(), linha.obra().nome());
    }

    private static BigDecimal paraValor(String texto) {
        if (texto == null || !texto.startsWith("R$")) {
            return null;
        }

        String limpo = texto.replace("R$", "")
                .replace(".", "")
                .replace(",", ".")
                .replaceAll("[^0-9.-]", "");

        if (limpo.isEmpty()) {
            return null;
        }

        return new BigDecimal(limpo);
    }
}
