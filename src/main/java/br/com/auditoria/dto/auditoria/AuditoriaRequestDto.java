package br.com.auditoria.dto.auditoria;

import jakarta.validation.constraints.NotBlank;

public record AuditoriaRequestDto(@NotBlank String obraId) {
}
