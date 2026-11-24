package br.com.auditoria.repository;

import br.com.auditoria.entity.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    Optional<Comunicado> findByAuditoriaId(Long auditoriaId);
}
