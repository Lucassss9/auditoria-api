package br.com.auditoria.dto.obra;

import br.com.auditoria.enums.Regiao;

public record FilialDto(String id, String codigo, String nome, Regiao regional) {
}
