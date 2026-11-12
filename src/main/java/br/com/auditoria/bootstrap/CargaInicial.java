package br.com.auditoria.bootstrap;

import br.com.auditoria.entity.Usuario;
import br.com.auditoria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {

    UsuarioRepository usuarioRepository;
    PasswordEncoder encoder;
    String email;
    String senha;

    public CargaInicial(UsuarioRepository  usuarioRepository, PasswordEncoder encoder,
                        @Value("${AUDITORIA_ADMIN_EMAIL}") String email, @Value("${AUDITORIA_ADMIN_SENHA}") String senha) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.email = email;
        this.senha = senha;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            usuarioRepository.save(new Usuario(null,"nome", email, encoder.encode(senha), "Teste"));
            System.out.println("Usuario adicionado com sucesso!");
        }
    }
}
