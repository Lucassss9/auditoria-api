package br.com.auditoria.service;

import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;

import java.util.List;

public interface CatalogoDeObras {

    List<ObraDto> listar();

    ObraDto buscarPorId(String id);

    List<FilialDto> listarFiliais();
}
