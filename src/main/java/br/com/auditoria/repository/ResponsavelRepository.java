package br.com.auditoria.repository;

import br.com.auditoria.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    List<Responsavel> findByObraId(String obra);

    Optional<Responsavel> findByEmailIgnoreCaseAndObraId(String email, String obraId);
}
