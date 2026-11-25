package br.com.auditoria.repository;

import br.com.auditoria.entity.Evidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByItemId(Long itemId);

    @Query("select e from Evidencia e where e.item.auditoria.id = :auditoriaId")
    List<Evidencia> findByAuditoriaId(@Param("auditoriaId") Long auditoriaId);
}
