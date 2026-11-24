package br.com.auditoria.dto.busca;

import java.util.List;

public record BuscaSalvaResponseDto(String id, String nome, List<String> termos) {
}
