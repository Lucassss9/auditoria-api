package br.com.auditoria.service;

import br.com.auditoria.dto.usuario.UsuarioResponseDto;
import br.com.auditoria.entity.Usuario;
import br.com.auditoria.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public UsuarioResponseDto porEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario nao encontrado: " + email));

        return new UsuarioResponseDto(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getCargo());
    }
}
