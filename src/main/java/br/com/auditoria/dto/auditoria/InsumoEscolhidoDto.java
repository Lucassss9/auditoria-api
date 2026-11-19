package br.com.auditoria.dto.auditoria;

import jakarta.validation.constraints.NotBlank;

public record InsumoEscolhidoDto(@NotBlank String insumoId, String lista) {
}
