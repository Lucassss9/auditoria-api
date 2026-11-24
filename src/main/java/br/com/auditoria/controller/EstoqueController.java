package br.com.auditoria.controller;

import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.service.EstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping("/estoque")
    public List<InsumoEstoqueDto> buscar(@RequestParam String obra,
                                         @RequestParam(defaultValue = "") String termo) {
        return estoqueService.buscar(obra, termo);
    }
}
