package br.com.auditoria.service;

import br.com.auditoria.client.CfObrasClient;
import br.com.auditoria.client.EstoqueMapper;
import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import br.com.auditoria.dto.cfobras.LinhaEstoqueCfDto;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.dto.obra.ObraDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private static final int POR_PAGINA = 100;

    private final CatalogoDeObras catalogo;
    private final CfObrasClient client;

    public List<InsumoEstoqueDto> lerEstoqueDaObra(ObraDto obra, String obraId) {
        List<InsumoEstoqueDto> itens = new ArrayList<>();
        int pulo = 0;

        while (true) {
            EstoqueCfDto pagina = client.buscarEstoque(obra.filialId(), POR_PAGINA, pulo);
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
        return itens.stream().filter(item -> item.obraId().equals(obraId)).toList();
    }

    public List<InsumoEstoqueDto> buscar(String obraId, String termo) {
        ObraDto obra = catalogo.buscarPorId(obraId);

        List<InsumoEstoqueDto> daObra = lerEstoqueDaObra(obra, obraId);
        String procurado = semAcento(termo);

        if (procurado.isEmpty()) {
            return daObra;
        }

        return daObra.stream()
                .filter(insumo -> semAcento(insumo.descricao()).contains(procurado)
                        || semAcento(insumo.codigo()).contains(procurado))
                .toList();
    }

    private static String semAcento(String texto) {
        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
