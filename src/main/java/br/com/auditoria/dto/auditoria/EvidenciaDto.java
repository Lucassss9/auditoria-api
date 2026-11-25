package br.com.auditoria.dto.auditoria;

import java.time.LocalDateTime;

public record EvidenciaDto(String id, String nomeOriginal, String url, long tamanho,
                           LocalDateTime enviadaEm) {
}
