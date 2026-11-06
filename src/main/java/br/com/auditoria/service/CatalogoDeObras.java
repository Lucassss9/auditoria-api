package br.com.auditoria.service;

import br.com.auditoria.dto.obra.ObraDto;

import java.util.List;

public interface CatalogoDeObras {

    public List<ObraDto> listar();
    public ObraDto buscarPorId(String id);
}
