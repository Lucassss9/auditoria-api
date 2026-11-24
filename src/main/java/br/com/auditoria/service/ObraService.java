package br.com.auditoria.service;

import br.com.auditoria.dto.obra.ContagemPorObra;
import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ObraService {

    private final CatalogoDeObras catalogo;
    private final AuditoriaRepository auditoriaRepository;

    @Transactional(readOnly = true)
    public List<ObraDto> listar() {
        return comTotais(catalogo.listar());
    }

    @Transactional(readOnly = true)
    public ObraDto buscarPorId(String id) {
        ObraDto obra = catalogo.buscarPorId(id);

        if (obra == null) {
            return null;
        }

        return comTotais(List.of(obra)).getFirst();
    }

    public List<FilialDto> listarFiliais() {
        return catalogo.listarFiliais();
    }

    private List<ObraDto> comTotais(List<ObraDto> obras) {
        Map<String, Long> porObra = new HashMap<>();

        for (ContagemPorObra contagem : auditoriaRepository.contarPorObra()) {
            porObra.put(contagem.obraId(), contagem.total());
        }

        List<ObraDto> comTotal = new ArrayList<>();

        for (ObraDto obra : obras) {
            comTotal.add(new ObraDto(obra.id(), obra.codigo(), obra.nome(), obra.segmento(),
                    obra.situacao(), obra.filialId(), obra.filial(),
                    porObra.getOrDefault(obra.id(), 0L).intValue()));
        }

        return List.copyOf(comTotal);
    }
}
