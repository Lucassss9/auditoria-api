package br.com.auditoria.repository;

import br.com.auditoria.dto.obra.ContagemPorObra;
import br.com.auditoria.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query("""
            select new br.com.auditoria.dto.obra.ContagemPorObra(a.obraId, count(a))
            from Auditoria a
            where a.situacaoAuditoria <> br.com.auditoria.enums.SituacaoAuditoria.CANCELADA
            group by a.obraId
            """)
    List<ContagemPorObra> contarPorObra();
}
