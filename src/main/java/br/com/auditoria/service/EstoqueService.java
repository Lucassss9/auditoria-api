package br.com.auditoria.service;

import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.dto.obra.ObraDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final CatalogoDeObras catalogo;
    private final EstoqueDoCf estoqueDoCf;

    public List<InsumoEstoqueDto> buscar(String obraId, String termo) {
        ObraDto obra = catalogo.buscarPorId(obraId);

        if (obra == null) {
            throw new NoSuchElementException("Obra nao encontrada: " + obraId);
        }

        List<InsumoEstoqueDto> daObra = daObra(estoqueDoCf.daFilialEmCache(obra.filialId()), obraId);
        String procurado = aparar(termo);

        if (procurado.isEmpty()) {
            return daObra;
        }

        boolean comAcento = !procurado.equals(semAcento(termo));
        String agulha = comAcento ? procurado : semAcento(termo);

        return daObra.stream()
                .filter(insumo -> combina(insumo.descricao(), agulha, comAcento)
                        || semAcento(insumo.codigo()).contains(semAcento(termo)))
                .toList();
    }

    private static boolean combina(String descricao, String agulha, boolean comAcento) {
        String texto = comAcento ? aparar(descricao) : semAcento(descricao);

        if (temDigito(agulha)) {
            return texto.contains(agulha);
        }

        int onde = texto.indexOf(agulha);

        while (onde >= 0) {
            if (onde == 0 || !Character.isLetterOrDigit(texto.charAt(onde - 1))) {
                return true;
            }

            onde = texto.indexOf(agulha, onde + 1);
        }

        return false;
    }

    private static String aparar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.toLowerCase(Locale.ROOT).trim();
    }

    private static boolean temDigito(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (Character.isDigit(texto.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    public List<InsumoEstoqueDto> lerParaCongelar(ObraDto obra, String obraId) {
        return daObra(estoqueDoCf.daFilial(obra.filialId()), obraId);
    }

    private static List<InsumoEstoqueDto> daObra(List<InsumoEstoqueDto> daFilial, String obraId) {
        return daFilial.stream().filter(insumo -> insumo.obraId().equals(obraId)).toList();
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
