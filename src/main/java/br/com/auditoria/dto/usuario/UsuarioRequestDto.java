package br.com.auditoria.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDto(@NotBlank(message = "Email tem que estar preenchido.") String email,
                                @NotBlank(message = "Senha tem que ser preenchida.")String senha) {
}
