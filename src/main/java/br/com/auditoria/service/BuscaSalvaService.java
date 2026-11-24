package br.com.auditoria.service;

import br.com.auditoria.dto.busca.BuscaSalvaRequestDto;
import br.com.auditoria.dto.busca.BuscaSalvaResponseDto;
import br.com.auditoria.entity.BuscaSalva;
import br.com.auditoria.repository.BuscaSalvaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BuscaSalvaService {

    private final BuscaSalvaRepository buscaSalvaRepository;

    @Transactional(readOnly = true)
    public List<BuscaSalvaResponseDto> listar() {
        return buscaSalvaRepository.findAll().stream().map(this::paraDto).toList();
    }

    @Transactional
    public BuscaSalvaResponseDto criar(BuscaSalvaRequestDto requestDto) {
        BuscaSalva busca = new BuscaSalva();
        preencher(requestDto, busca);

        return paraDto(buscaSalvaRepository.save(busca));
    }

    @Transactional
    public BuscaSalvaResponseDto atualizar(Long id, BuscaSalvaRequestDto requestDto) {
        BuscaSalva busca = buscaSalvaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Busca salva nao encontrada: " + id));

        preencher(requestDto, busca);

        return paraDto(buscaSalvaRepository.save(busca));
    }

    @Transactional
    public void remover(Long id) {
        if (!buscaSalvaRepository.existsById(id)) {
            throw new NoSuchElementException("Busca salva nao encontrada: " + id);
        }

        buscaSalvaRepository.deleteById(id);
    }

    private void preencher(BuscaSalvaRequestDto requestDto, BuscaSalva busca) {
        busca.setNome(requestDto.nome().trim());

        List<String> limpos = new ArrayList<>();

        for (String termo : requestDto.termos()) {
            String limpo = termo.trim();

            if (!limpo.isEmpty() && !limpos.contains(limpo)) {
                limpos.add(limpo);
            }
        }

        busca.getTermos().clear();
        busca.getTermos().addAll(limpos);
    }

    private BuscaSalvaResponseDto paraDto(BuscaSalva busca) {
        return new BuscaSalvaResponseDto(String.valueOf(busca.getId()), busca.getNome(),
                List.copyOf(busca.getTermos()));
    }
}
