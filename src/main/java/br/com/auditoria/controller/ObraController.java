package br.com.auditoria.controller;

import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.enums.Situacao;
import br.com.auditoria.service.ObraService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ObraController {

    private final ObraService obraService;

    public ObraController(ObraService obraService) {
        this.obraService = obraService;
    }

    @GetMapping("/obras")
    public List<ObraDto> listar(@RequestParam(required = false) Situacao situacao) {
        return obraService.listar(situacao);
    }

    @GetMapping("/obras/{id}")
    public ObraDto buscarPorId(@PathVariable String id) {
        return obraService.buscarPorId(id);
    }

    @GetMapping("/filiais")
    public List<FilialDto> listarFiliais() {
        return obraService.listarFiliais();
    }
}
