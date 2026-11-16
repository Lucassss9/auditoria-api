package br.com.auditoria.service;

import br.com.auditoria.entity.Responsavel;
import br.com.auditoria.repository.ResponsavelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    public List<Responsavel> listarTodos() {
        return responsavelRepository.findAll();
    }

    public List<Responsavel> listarPorObra(String obraId) {
        return responsavelRepository.findByObraId(obraId);
    }
}
