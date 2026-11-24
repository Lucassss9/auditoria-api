package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.InsumoCfDto;
import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class EstoqueMapper {

    public static boolean utilizavel(LinhaEstoqueCfDto linha) {
        return linha != null
                && linha.insumo() != null
                && linha.insumo()._id() != null
                && !linha.insumo()._id().isBlank()
                && linha.obra() != null
                && linha.obra()._id() != null
                && linha.qtde() != null;
    }

    public static InsumoEstoqueDto mapear(LinhaEstoqueCfDto linha) {
        return new InsumoEstoqueDto(linha.insumo()._id(), linha.insumo().codigo(), nomeCompleto(linha.insumo()),
                linha.qtde().setScale(3, RoundingMode.HALF_UP), linha.insumo().unidade(), paraValor(linha.valor_unitario()), linha.obra()._id(), linha.obra().nome());
    }

    private static String nomeCompleto(InsumoCfDto insumo) {
        String pai = aparado(insumo.descricao_pai());
        String proprio = aparado(insumo.descricao());

        if (pai == null) {
            return proprio;
        }

        if (proprio == null) {
            return pai;
        }

        if (semRuido(proprio).contains(semRuido(pai))) {
            return proprio;
        }

        return pai + " - " + proprio;
    }

    private static String semRuido(String texto) {
        return texto.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static String aparado(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
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
