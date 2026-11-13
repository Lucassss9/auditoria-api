package br.com.auditoria.controller;

import br.com.auditoria.dto.usuario.UsuarioRequestDto;
import br.com.auditoria.dto.usuario.UsuarioResponseDto;
import br.com.auditoria.entity.Usuario;
import br.com.auditoria.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    private final AuthenticationManager manager;
    private final UsuarioRepository usuarioRepository;
    private final SecurityContextRepository cofre = new HttpSessionSecurityContextRepository();

    public UsuarioController(AuthenticationManager manager, UsuarioRepository usuarioRepository) {
        this.manager = manager;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public UsuarioResponseDto login(@Valid @RequestBody UsuarioRequestDto usuarioRequestDto,
                                    HttpServletRequest request, HttpServletResponse response) {

        Authentication identidade = manager.authenticate(UsernamePasswordAuthenticationToken
                .unauthenticated(usuarioRequestDto.email(), usuarioRequestDto.senha()));

        SecurityContext envelope = SecurityContextHolder.createEmptyContext();
        envelope.setAuthentication(identidade);
        SecurityContextHolder.setContext(envelope);
        cofre.saveContext(envelope, request, response);

        Usuario usuario = usuarioRepository.findByEmail(identidade.getName())
                .orElseThrow();

        return new UsuarioResponseDto(usuario.getId(), usuario.getNome(),
                usuario.getEmail(),  usuario.getCargo());
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        request.getSession(false).invalidate();
    }

    @GetMapping("/sessao")
    public UsuarioResponseDto sessao() {
        Authentication identidade = SecurityContextHolder.getContext().getAuthentication();

        Usuario usuario = usuarioRepository.findByEmail(identidade.getName())
                .orElseThrow();

        return new UsuarioResponseDto(usuario.getId(), usuario.getNome(),
                usuario.getEmail(),  usuario.getCargo());
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}
