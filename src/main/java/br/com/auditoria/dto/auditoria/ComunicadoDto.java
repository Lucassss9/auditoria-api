package br.com.auditoria.dto.auditoria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ComunicadoDto(List<String> destinatariosIds, LocalDate prazo, String assunto,
                            String mensagem, LocalDateTime geradoEm) {
}
