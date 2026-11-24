package br.com.auditoria.dto.busca;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BuscaSalvaRequestDto(@NotBlank String nome,
                                   @NotEmpty List<@NotBlank String> termos) {
}
