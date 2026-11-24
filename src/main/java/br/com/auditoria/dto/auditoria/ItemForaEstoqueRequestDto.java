package br.com.auditoria.dto.auditoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ItemForaEstoqueRequestDto(@NotBlank String descricao, String codigo,
                                        @NotBlank String unidade,
                                        @NotNull @Positive BigDecimal quantidade,
                                        @PositiveOrZero BigDecimal valorUnitario,
                                        String local, String observacao) {
}
