package br.com.auditoria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

@Component
public class ArmazemDeEvidencias {

    public static final long TAMANHO_MAXIMO = 8L * 1024 * 1024;

    private static final Map<String, String> EXTENSOES = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp");

    private final Path pasta;

    public ArmazemDeEvidencias(@Value("${evidencias.pasta:evidencias}") String pasta) {
        this.pasta = Path.of(pasta).toAbsolutePath().normalize();
    }

    public boolean tipoAceito(String tipo) {
        return tipo != null && EXTENSOES.containsKey(tipo.toLowerCase());
    }

    public String guardar(MultipartFile arquivo) {
        String nome = UUID.randomUUID() + EXTENSOES.get(arquivo.getContentType().toLowerCase());

        try {
            Files.createDirectories(pasta);

            try (InputStream entrada = arquivo.getInputStream()) {
                Files.copy(entrada, pasta.resolve(nome), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException falha) {
            throw new UncheckedIOException("Nao foi possivel guardar a evidencia.", falha);
        }

        return nome;
    }

    public Path caminhoDe(String nome) {
        Path alvo = pasta.resolve(nome).normalize();

        if (!alvo.startsWith(pasta)) {
            throw new IllegalArgumentException("Arquivo fora da pasta de evidencias: " + nome);
        }

        return alvo;
    }

    public void apagar(String nome) {
        try {
            Files.deleteIfExists(caminhoDe(nome));
        } catch (IOException falha) {
            throw new UncheckedIOException("Nao foi possivel apagar a evidencia.", falha);
        }
    }
}
