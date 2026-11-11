package br.com.auditoria.controller;

import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.enums.Situacao;
import br.com.auditoria.service.CatalogoDeObras;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ObraController {

    private final CatalogoDeObras catalogo;

    public ObraController(CatalogoDeObras catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/obras")
    public List<ObraDto> listar(@RequestParam(required = false) Situacao situacao) {
        List<ObraDto> obras = catalogo.listar();

        if (situacao == null) {
            return obras;
        }

        return obras.stream().filter(obra -> obra.situacao() == situacao).toList();
    }

    @GetMapping("/obras/{id}")
    public ResponseEntity<ObraDto> buscarPorId(@PathVariable String id) {
        ObraDto obra = catalogo.buscarPorId(id);

        return obra == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(obra);
    }

    @GetMapping("/filiais")
    public List<FilialDto> listarFiliais() {
        return catalogo.listarFiliais();
    }
}
