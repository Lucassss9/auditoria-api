package br.com.auditoria.dto.auditoria;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AuditoriaRequestDto(@NotBlank String obraId, @NotBlank String responsavel, @Valid @NotEmpty List<InsumoEscolhidoDto> insumos) {
}
