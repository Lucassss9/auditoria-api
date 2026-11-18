package br.com.auditoria.dto.auditoria;

import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.SituacaoAuditoria;

import java.time.LocalDateTime;

public record AuditoriaResponseDto(Long id, String obraId, String obraNome, Segmento segmento,
                                   SituacaoAuditoria situacaoAuditoria, LocalDateTime criadaEm,
                                   LocalDateTime referenciaEm, int totalDeItens) {
}
