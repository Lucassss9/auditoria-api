package br.com.auditoria.controller;

import br.com.auditoria.dto.responsavel.ResponsavelRequestDto;
import br.com.auditoria.dto.responsavel.ResponsavelResponseDto;
import br.com.auditoria.service.ResponsavelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping("/responsaveis")
    public List<ResponsavelResponseDto> listarResponsaveis(@RequestParam(required = false) String obra) {
        return obra == null ? responsavelService.listarTodos() : responsavelService.listarPorObra(obra);
    }

    @PostMapping("/responsaveis")
    public ResponsavelResponseDto criarResponsavel(@Valid @RequestBody ResponsavelRequestDto responsavelRequestDto) {
        return responsavelService.criarResponsavel(responsavelRequestDto);
    }

    @PutMapping("/responsaveis/{id}")
    public ResponsavelResponseDto atualizarResponsavel(@Valid @RequestBody ResponsavelRequestDto responsavelRequestDto, @PathVariable Long id) {
        return responsavelService.atualizarResponsavel(responsavelRequestDto, id);
    }

    @DeleteMapping("/responsaveis/{id}")
    public void deleteResponsavel(@PathVariable Long id) {
        responsavelService.deletarResponsavel(id);
    }
}
