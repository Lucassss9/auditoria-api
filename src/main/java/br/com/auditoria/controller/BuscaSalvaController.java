package br.com.auditoria.controller;

import br.com.auditoria.dto.busca.BuscaSalvaRequestDto;
import br.com.auditoria.dto.busca.BuscaSalvaResponseDto;
import br.com.auditoria.service.BuscaSalvaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BuscaSalvaController {

    private final BuscaSalvaService buscaSalvaService;

    @GetMapping("/listas-favoritas")
    public List<BuscaSalvaResponseDto> listar() {
        return buscaSalvaService.listar();
    }

    @PostMapping("/listas-favoritas")
    @ResponseStatus(HttpStatus.CREATED)
    public BuscaSalvaResponseDto criar(@Valid @RequestBody BuscaSalvaRequestDto requestDto) {
        return buscaSalvaService.criar(requestDto);
    }

    @PutMapping("/listas-favoritas/{id}")
    public BuscaSalvaResponseDto atualizar(@PathVariable Long id,
                                           @Valid @RequestBody BuscaSalvaRequestDto requestDto) {
        return buscaSalvaService.atualizar(id, requestDto);
    }

    @DeleteMapping("/listas-favoritas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        buscaSalvaService.remover(id);
    }
}
