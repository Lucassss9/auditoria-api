package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.InsumoCfDto;
import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.cfobras.ObraCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    private LinhaEstoqueCfDto linhaCom(BigDecimal qtde, String preco) {
        InsumoCfDto insumo = new InsumoCfDto(
                "aaaaaaaaaaaaaaaaaaaaaaaa", "Insumo de Teste", "4873", "un");

        ObraCfDto obra = new ObraCfDto("bbbbbbbbbbbbbbbbbbbbbbbb", "OBRA DE TESTE");

        return new LinhaEstoqueCfDto(insumo, obra, qtde, preco);
    }
}
