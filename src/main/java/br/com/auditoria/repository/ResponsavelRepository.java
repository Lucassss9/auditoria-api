package br.com.auditoria.repository;

import br.com.auditoria.entity.Responsavel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository {
    Optional<Responsavel> getResponsavel(Responsavel responsavel);
}
