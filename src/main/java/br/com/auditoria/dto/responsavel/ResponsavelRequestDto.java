package br.com.auditoria.dto.responsavel;

import br.com.auditoria.enums.FuncaoObra;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResponsavelRequestDto(@NotBlank String obraId, @NotBlank String nome, @NotNull FuncaoObra funcao,
                                    @NotBlank @Email String email, boolean ativo, boolean receberEmail) {
}
