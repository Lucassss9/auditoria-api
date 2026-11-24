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
        String procurado = semAcento(termo);

        if (procurado.isEmpty()) {
            return daObra;
        }

        return daObra.stream()
                .filter(insumo -> semAcento(insumo.descricao()).contains(procurado)
                        || semAcento(insumo.codigo()).contains(procurado))
                .toList();
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
