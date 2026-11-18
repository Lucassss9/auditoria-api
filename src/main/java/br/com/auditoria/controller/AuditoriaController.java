package br.com.auditoria.controller;

import br.com.auditoria.dto.auditoria.AuditoriaRequestDto;
import br.com.auditoria.dto.auditoria.AuditoriaResponseDto;
import br.com.auditoria.service.AuditoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @PostMapping("/auditorias")
    @ResponseStatus(HttpStatus.CREATED)
    public AuditoriaResponseDto criar(@Valid @RequestBody AuditoriaRequestDto requestDto) {
        return auditoriaService.criar(requestDto);
    }
}