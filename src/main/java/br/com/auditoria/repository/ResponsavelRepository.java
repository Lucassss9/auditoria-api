package br.com.auditoria.repository;

import br.com.auditoria.entity.Responsavel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    List<Responsavel> findByObra(String obra);
}
