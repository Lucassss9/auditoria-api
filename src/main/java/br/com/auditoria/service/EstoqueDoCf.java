package br.com.auditoria.service;

import br.com.auditoria.client.CfObrasClient;
import br.com.auditoria.client.EstoqueMapper;
import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstoqueDoCf {

    public static final String CACHE = "estoqueDaFilial";

    private static final int POR_PAGINA = 100;

    private final CfObrasClient client;

    @Cacheable(CACHE)
    public List<InsumoEstoqueDto> daFilialEmCache(String filialId) {
        return daFilial(filialId);
    }

    public List<InsumoEstoqueDto> daFilial(String filialId) {
        List<InsumoEstoqueDto> itens = new ArrayList<>();
        int pulo = 0;

        while (true) {
            EstoqueCfDto pagina = client.buscarEstoque(filialId, POR_PAGINA, pulo);
            List<LinhaEstoqueCfDto> linhas = pagina.data().results();

            if (linhas.isEmpty()) {
                break;
            }

            for (LinhaEstoqueCfDto linha : linhas) {
                itens.add(EstoqueMapper.mapear(linha));
            }

            pulo += linhas.size();

            if (itens.size() >= pagina.data().count()) {
                break;
            }
        }

        return List.copyOf(itens);
    }
}
