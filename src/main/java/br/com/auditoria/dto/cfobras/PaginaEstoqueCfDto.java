package br.com.auditoria.dto.cfobras;

import java.util.List;

public record PaginaEstoqueCfDto(List<LinhaEstoqueCfDto> results, int count) {
}
