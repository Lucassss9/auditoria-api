package br.com.auditoria.repository;

import br.com.auditoria.entity.BuscaSalva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuscaSalvaRepository extends JpaRepository<BuscaSalva, Long> {
}
