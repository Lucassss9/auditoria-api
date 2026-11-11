package br.com.auditoria.bootstrap;

import br.com.auditoria.entity.Usuario;
import br.com.auditoria.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CargaInicial implements CommandLineRunner {

    UsuarioRepository usuarioRepository;
    PasswordEncoder encoder;

    public CargaInicial(UsuarioRepository  usuarioRepository, PasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail("seuemail@gmail.com").isEmpty()) {
            usuarioRepository.save(new Usuario(null,"nome", "seuemail@gmail.com", encoder.encode("1234"), "Teste"));
            System.out.println("Usuario adicionado com sucesso!");
        }
    }
}
