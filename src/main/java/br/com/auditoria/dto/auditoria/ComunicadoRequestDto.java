package br.com.auditoria.dto.auditoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record ComunicadoRequestDto(@NotEmpty List<@NotBlank String> destinatariosIds,
                                   @NotNull LocalDate prazo, @NotBlank String assunto,
                                   @NotBlank String mensagem) {
}
