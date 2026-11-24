package br.com.auditoria.repository;

import br.com.auditoria.entity.ItemForaEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemForaEstoqueRepository extends JpaRepository<ItemForaEstoque, Long> {

    List<ItemForaEstoque> findByAuditoriaId(Long auditoriaId);
}
