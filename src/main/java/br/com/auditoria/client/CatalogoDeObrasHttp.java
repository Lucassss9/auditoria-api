package br.com.auditoria.client;

import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.service.CatalogoDeObras;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CatalogoDeObrasHttp implements CatalogoDeObras {

    private final ObrasDoCf obrasDoCf;

    @Override
    public List<ObraDto> listar() {
        return obrasDoCf.todas();
    }

    @Override
    public ObraDto buscarPorId(String id) {
        for (ObraDto obra : obrasDoCf.todas()) {
            if (obra.id().equals(id)) {
                return obra;
            }
        }

        return null;
    }

    @Override
    public List<FilialDto> listarFiliais() {
        Map<String, FilialDto> porId = new LinkedHashMap<>();

        for (ObraDto obra : obrasDoCf.todas()) {
            if (obra.filial() != null) {
                porId.putIfAbsent(obra.filial().id(), obra.filial());
            }
        }

        return List.copyOf(porId.values());
    }
}
