package br.com.auditoria.repository;

import br.com.auditoria.entity.ItemAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemAuditoriaRepository extends JpaRepository<ItemAuditoria, Long> {

    List<ItemAuditoria> findByAuditoriaId(Long auditoriaId);
}
