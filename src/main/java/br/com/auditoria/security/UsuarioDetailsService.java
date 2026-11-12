package br.com.auditoria.security;

import br.com.auditoria.entity.Usuario;
import br.com.auditoria.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;


@Service
public class UsuarioDetailsService implements UserDetailsService {


    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Usuario usuario = usuarioRepository.findByEmail(username)
               .orElseThrow(() -> new UsernameNotFoundException("Credenciais invalidas."));

       return User.builder()
               .username(usuario.getEmail())
               .password(usuario.getSenha())
               .authorities(List.of())
               .build();
    }
}
