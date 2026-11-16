package br.com.auditoria.controller;

import br.com.auditoria.entity.Responsavel;
import br.com.auditoria.service.ResponsavelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping("/responsaveis")
    public List<Responsavel> listarResponsaveis(@RequestParam(required = false) String obra) {
        return obra == null ? responsavelService.listarTodos() : responsavelService.listarPorObra(obra);
    }
}
