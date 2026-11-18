package br.com.auditoria.dto.auditoria;

import java.time.LocalDateTime;

public record ReferenciaEstoqueDto(String origem, LocalDateTime capturadoEm, int totalItens) {
}
