package br.com.auditoria.dto.responsavel;

import br.com.auditoria.enums.FuncaoObra;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResponsavelRequestDto(@NotEmpty List<@NotBlank String> obraIds, @NotBlank String nome,
                                    @NotNull FuncaoObra funcao, @NotBlank @Email String email,
                                    boolean ativo, boolean receberEmail) {
}
