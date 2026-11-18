package br.com.auditoria.repository;

import br.com.auditoria.entity.ItemAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemAuditoriaRepository extends JpaRepository<ItemAuditoria, Long> {
}
