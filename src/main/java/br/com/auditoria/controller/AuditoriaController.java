package br.com.auditoria.controller;

import br.com.auditoria.dto.auditoria.AuditoriaDetalheDto;
import br.com.auditoria.dto.auditoria.AuditoriaRequestDto;
import br.com.auditoria.dto.auditoria.ComunicadoRequestDto;
import br.com.auditoria.dto.auditoria.ContagemItemDto;
import br.com.auditoria.dto.auditoria.ItemForaEstoqueRequestDto;
import br.com.auditoria.service.AuditoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @PostMapping("/auditorias")
    @ResponseStatus(HttpStatus.CREATED)
    public AuditoriaDetalheDto criar(@Valid @RequestBody AuditoriaRequestDto requestDto) {
        return auditoriaService.criar(requestDto);
    }

    @GetMapping("/auditorias/{id}")
    public ResponseEntity<AuditoriaDetalheDto> buscarPorId(@PathVariable Long id) {
        AuditoriaDetalheDto auditoria = auditoriaService.buscarPorId(id);

        return auditoria == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(auditoria);
    }

    @GetMapping("/auditorias")
    public List<AuditoriaDetalheDto> buscarTodos() {
        return auditoriaService.listarAuditorias();
    }

    @PatchMapping("/auditorias/{auditoriaId}/itens/{itemId}")
    public AuditoriaDetalheDto contar(@PathVariable Long auditoriaId, @PathVariable Long itemId,
                                      @RequestBody ContagemItemDto contagem) {
        return auditoriaService.contar(auditoriaId, itemId, contagem);
    }

    @PostMapping("/auditorias/{auditoriaId}/fora-do-estoque")
    @ResponseStatus(HttpStatus.CREATED)
    public AuditoriaDetalheDto adicionarForaDoEstoque(
            @PathVariable Long auditoriaId,
            @Valid @RequestBody ItemForaEstoqueRequestDto requestDto) {
        return auditoriaService.adicionarForaDoEstoque(auditoriaId, requestDto);
    }

    @PutMapping("/auditorias/{auditoriaId}/fora-do-estoque/{itemId}")
    public AuditoriaDetalheDto atualizarForaDoEstoque(
            @PathVariable Long auditoriaId, @PathVariable Long itemId,
            @Valid @RequestBody ItemForaEstoqueRequestDto requestDto) {
        return auditoriaService.atualizarForaDoEstoque(auditoriaId, itemId, requestDto);
    }

    @DeleteMapping("/auditorias/{auditoriaId}/fora-do-estoque/{itemId}")
    public AuditoriaDetalheDto removerForaDoEstoque(@PathVariable Long auditoriaId,
                                                    @PathVariable Long itemId) {
        return auditoriaService.removerForaDoEstoque(auditoriaId, itemId);
    }

    @PutMapping("/auditorias/{auditoriaId}/comunicado")
    public AuditoriaDetalheDto salvarComunicado(
            @PathVariable Long auditoriaId,
            @Valid @RequestBody ComunicadoRequestDto requestDto) {
        return auditoriaService.salvarComunicado(auditoriaId, requestDto);
    }

    @PostMapping("/auditorias/{id}/concluir")
    public AuditoriaDetalheDto concluir(@PathVariable Long id) {
        return auditoriaService.concluir(id);
    }

    @PostMapping("/auditorias/{id}/cancelar")
    public AuditoriaDetalheDto cancelar(@PathVariable Long id) {
        return auditoriaService.cancelar(id);
    }
}