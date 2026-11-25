package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.InsumoCfDto;
import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.cfobras.ObraCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EstoqueMapperTest {

    @Test
    void convertePrecoBrasileiroEmNumero() {
        InsumoEstoqueDto dto = EstoqueMapper.mapear(linhaCom(new BigDecimal("10"), "R$ 3.575,04"));

        assertEquals(new BigDecimal("3575.04"), dto.valorUnitario());
    }

    @Test
    void devolveNuloQuandoOPrecoNaoEstaCadastrado() {
        InsumoEstoqueDto dto = EstoqueMapper.mapear(linhaCom(new BigDecimal("10"), "valor não cadastrado"));

        assertNull(dto.valorUnitario(), "preco sem cadastro tem que virar nulo, e nao zero");
    }

    @Test
    void arredondaAQuantidadeEmTresCasas() {
        InsumoEstoqueDto dto = EstoqueMapper.mapear(
                linhaCom(new BigDecimal("4.547473508864641E-13"), "R$ 81,29"));

        assertEquals(new BigDecimal("0.000"), dto.quantidade());
    }

    @Test
    void copiaOsCamposDoInsumoEDaObra() {
        InsumoEstoqueDto dto = EstoqueMapper.mapear(linhaCom(new BigDecimal("10"), "R$ 6,08"));

        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaa", dto.id());
        assertEquals("4873", dto.codigo());
        assertEquals("Insumo de Teste", dto.descricao());
        assertEquals("un", dto.unidade());
        assertEquals("bbbbbbbbbbbbbbbbbbbbbbbb", dto.obraId());
        assertEquals("OBRA DE TESTE", dto.obraNome());
    }

    @Test
    void juntaONomeDoItemPaiQuandoEleExiste() {
        InsumoEstoqueDto dto = EstoqueMapper.mapear(
                linhaCom(new BigDecimal("10"), "R$ 6,08", "Tinta Spray", "4658"));

        assertEquals("Tinta Spray - Insumo de Teste", dto.descricao());
    }

    @Test
    void naoRepeteOPaiQuandoADescricaoJaOCarrega() {
        LinhaEstoqueCfDto linha = new LinhaEstoqueCfDto(
                new InsumoCfDto("aaaaaaaaaaaaaaaaaaaaaaaa", "Tela Morlan - Para Alvenaria",
                        "12652.8", "un", "Tela Morlan", "12652"),
                new ObraCfDto("bbbbbbbbbbbbbbbbbbbbbbbb", "OBRA DE TESTE"),
                new BigDecimal("10"), "R$ 6,08");

        assertEquals("Tela Morlan - Para Alvenaria", EstoqueMapper.mapear(linha).descricao());
    }

    @Test
    void descartaALinhaSemInsumoCadastrado() {
        LinhaEstoqueCfDto linha = new LinhaEstoqueCfDto(
                new InsumoCfDto(null, null, null, null, null, null),
                new ObraCfDto("bbbbbbbbbbbbbbbbbbbbbbbb", "OBRA DE TESTE"),
                new BigDecimal("10"), "R$ 6,08");

        assertFalse(EstoqueMapper.utilizavel(linha), "linha sem insumo nao pode entrar na lista");
    }

    @Test
    void aceitaALinhaCompleta() {
        assertTrue(EstoqueMapper.utilizavel(linhaCom(new BigDecimal("10"), "R$ 6,08")));
    }

    private LinhaEstoqueCfDto linhaCom(BigDecimal qtde, String preco) {
        return linhaCom(qtde, preco, null, null);
    }

    private LinhaEstoqueCfDto linhaCom(BigDecimal qtde, String preco, String descricaoPai,
                                       String codigoPai) {
        InsumoCfDto insumo = new InsumoCfDto(
                "aaaaaaaaaaaaaaaaaaaaaaaa", "Insumo de Teste", "4873", "un",
                descricaoPai, codigoPai);

        ObraCfDto obra = new ObraCfDto("bbbbbbbbbbbbbbbbbbbbbbbb", "OBRA DE TESTE");

        return new LinhaEstoqueCfDto(insumo, obra, qtde, preco);
    }
}
