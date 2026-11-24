package br.com.auditoria.dto.auditoria;

import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.SituacaoAuditoria;

import java.time.LocalDateTime;
import java.util.List;

public record AuditoriaDetalheDto(String id, String obraId, String obraNome, Segmento segmento,
                                  String filialNome,
                                  String responsavel, SituacaoAuditoria status,
                                  LocalDateTime iniciadaEm, LocalDateTime concluidaEm,
                                  ReferenciaEstoqueDto referencia, List<ItemAuditoriaDto> itens,
                                  List<ItemForaEstoqueDto> foraDoEstoque, ComunicadoDto comunicado) {
}
