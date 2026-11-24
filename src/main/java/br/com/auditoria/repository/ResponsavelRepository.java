package br.com.auditoria.repository;

import br.com.auditoria.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    @Query("select r from Responsavel r join r.obraIds o where o = :obraId")
    List<Responsavel> findByObraId(@Param("obraId") String obraId);

    Optional<Responsavel> findByEmailIgnoreCase(String email);
}
