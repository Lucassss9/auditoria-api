package br.com.auditoria.service;

import br.com.auditoria.dto.auditoria.*;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.entity.Auditoria;
import br.com.auditoria.entity.ItemAuditoria;
import br.com.auditoria.enums.SituacaoAuditoria;
import br.com.auditoria.enums.SituacaoItem;
import br.com.auditoria.exception.ConflitoException;
import br.com.auditoria.repository.AuditoriaRepository;
import br.com.auditoria.repository.ItemAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final EstoqueService estoqueService;
    private final CatalogoDeObras catalogo;
    private final AuditoriaRepository auditoriaRepository;
    private final ItemAuditoriaRepository itemAuditoriaRepository;

    @Transactional
    public AuditoriaDetalheDto criar(AuditoriaRequestDto requestDto) {
        ObraDto obra = catalogo.buscarPorId(requestDto.obraId());

        if (obra == null) {
            throw new IllegalArgumentException("Obra nao encontrada: " + requestDto.obraId());
        }

        Set<String> escolhidos = requestDto.insumos().stream().map(InsumoEscolhidoDto::insumoId)
                .collect(Collectors.toSet());

        Map<String, String> listas = new HashMap<>();

        for (InsumoEscolhidoDto escolhido : requestDto.insumos()) {
            listas.put(escolhido.insumoId(), escolhido.lista());
        }

        LocalDateTime referenciaEm = LocalDateTime.now();
        List<InsumoEstoqueDto> daObra = estoqueService.lerEstoqueDaObra(obra, requestDto.obraId());

        daObra = daObra.stream().filter(insumo -> escolhidos.contains(insumo.id())).toList();

        Auditoria salva = auditoriaRepository.save(montarAuditoria(obra, requestDto, referenciaEm));
        List<ItemAuditoria> itens = montarItens(daObra, salva, listas);

        itemAuditoriaRepository.saveAll(itens);

        return paraDetalhe(salva, itens);
    }

    @Transactional(readOnly = true)
    public AuditoriaDetalheDto buscarPorId(Long id) {
        Auditoria auditoria = auditoriaRepository.findById(id).orElse(null);

        if (auditoria == null) {
            return null;
        }

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(id));
    }

    @Transactional(readOnly = true)
    public List<AuditoriaDetalheDto> listarAuditorias() {
       return auditoriaRepository.findAll().stream().map(auditoria -> paraDetalhe(auditoria,
               itemAuditoriaRepository.findByAuditoriaId(auditoria.getId()))).toList();
    }

    @Transactional
    public AuditoriaDetalheDto contar(Long auditoriaId, Long itemId, ContagemItemDto contagem) {
        Auditoria auditoria = exigirAuditoria(auditoriaId);
        exigirEmAndamento(auditoria);

        ItemAuditoria item = itemAuditoriaRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item nao encontrado: " + itemId));

        if (!item.getAuditoria().getId().equals(auditoriaId)) {
            throw new NoSuchElementException("Item nao encontrado: " + itemId);
        }

        validarContagem(contagem);

        item.setQuantidadeContada(contagem.quantidadeReal());
        item.setSituacaoItem(contagem.situacao());
        item.setMotivo(contagem.motivoNaoContagem());
        item.setLocal(contagem.local());
        item.setObservacao(contagem.observacao());

        itemAuditoriaRepository.save(item);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(auditoriaId));
    }

    @Transactional
    public AuditoriaDetalheDto concluir(Long id) {
        Auditoria auditoria = exigirAuditoria(id);
        exigirEmAndamento(auditoria);

        auditoria.setSituacaoAuditoria(SituacaoAuditoria.CONCLUIDA);
        auditoria.setConcluidaEm(LocalDateTime.now());
        auditoriaRepository.save(auditoria);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(id));
    }

    @Transactional
    public AuditoriaDetalheDto cancelar(Long id) {
        Auditoria auditoria = exigirAuditoria(id);
        exigirEmAndamento(auditoria);

        auditoria.setSituacaoAuditoria(SituacaoAuditoria.CANCELADA);
        auditoriaRepository.save(auditoria);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(id));
    }

    private Auditoria exigirAuditoria(Long id) {
        return auditoriaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Auditoria nao encontrada: " + id));
    }

    private void exigirEmAndamento(Auditoria auditoria) {
        if (auditoria.getSituacaoAuditoria() != SituacaoAuditoria.EM_ANDAMENTO) {
            throw new ConflitoException(
                    "A auditoria " + auditoria.getId() + " nao esta mais em andamento.");
        }
    }

    private void validarContagem(ContagemItemDto contagem) {
        if (contagem.situacao() == null) {
            throw new IllegalArgumentException("A situacao do item e obrigatoria.");
        }

        if (contagem.quantidadeReal() != null && contagem.quantidadeReal().signum() < 0) {
            throw new IllegalArgumentException("A quantidade contada nao pode ser negativa.");
        }

        switch (contagem.situacao()) {
            case CONFERIDO -> {
                if (contagem.quantidadeReal() == null) {
                    throw new IllegalArgumentException(
                            "Item conferido precisa da quantidade contada.");
                }

                if (contagem.motivoNaoContagem() != null) {
                    throw new IllegalArgumentException(
                            "Item conferido nao tem motivo de nao contagem.");
                }
            }
            case NAO_CONTADO -> {
                if (contagem.motivoNaoContagem() == null) {
                    throw new IllegalArgumentException("Item nao contado precisa de um motivo.");
                }

                if (contagem.quantidadeReal() != null) {
                    throw new IllegalArgumentException(
                            "Item nao contado nao tem quantidade contada.");
                }
            }
            case NAO_CONFERIDO -> {
                if (contagem.quantidadeReal() != null || contagem.motivoNaoContagem() != null) {
                    throw new IllegalArgumentException(
                            "Item nao conferido nao tem quantidade nem motivo.");
                }
            }
        }
    }

    private AuditoriaDetalheDto paraDetalhe(Auditoria auditoria, List<ItemAuditoria> itens) {
        List<ItemAuditoriaDto> itensDto = itens.stream().map(this::paraItemDto).toList();

        ReferenciaEstoqueDto referencia =
                new ReferenciaEstoqueDto("CF Obras", auditoria.getReferenciaEm(), itensDto.size());

        return new AuditoriaDetalheDto(String.valueOf(auditoria.getId()), auditoria.getObraId(),
                auditoria.getObraNome(), auditoria.getFilialNome(), auditoria.getResponsavel(),
                auditoria.getSituacaoAuditoria(), auditoria.getCriadaEm(), auditoria.getConcluidaEm(),
                referencia, itensDto, List.of(), null);
    }

    private ItemAuditoriaDto paraItemDto(ItemAuditoria item) {
        return new ItemAuditoriaDto(String.valueOf(item.getId()), item.getCodigo(),
                item.getDescricao(), item.getUnidade(), item.getQuantidadeCongelada(),
                item.getValorUnitario(), item.getLista(), item.getQuantidadeContada(),
                item.getSituacaoItem(), item.getMotivo(), item.getLocal(),
                item.getObservacao() == null ? "" : item.getObservacao());
    }


    private Auditoria montarAuditoria(ObraDto obra, AuditoriaRequestDto requestDto, LocalDateTime referenciaEm) {
        Auditoria auditoria = new Auditoria();

        auditoria.setObraId(obra.id());
        auditoria.setObraNome(obra.nome());
        auditoria.setSegmento(obra.segmento());
        auditoria.setFilialId(obra.filialId());
        auditoria.setSituacaoAuditoria(SituacaoAuditoria.EM_ANDAMENTO);
        auditoria.setCriadaEm(LocalDateTime.now());
        auditoria.setFilialNome(obra.filial() == null ? null : obra.filial().nome());
        auditoria.setResponsavel(requestDto.responsavel());
        auditoria.setReferenciaEm(referenciaEm);

        return auditoria;
    }

    private List<ItemAuditoria> montarItens(List<InsumoEstoqueDto> insumos, Auditoria auditoria,
                                            Map<String, String> listas) {
        List<ItemAuditoria> itens = new ArrayList<>();

        for (InsumoEstoqueDto insumo : insumos) {
            ItemAuditoria item = new ItemAuditoria();

            item.setAuditoria(auditoria);
            item.setInsumoId(insumo.id());
            item.setCodigo(insumo.codigo());
            item.setDescricao(insumo.descricao());
            item.setUnidade(insumo.unidade());
            item.setQuantidadeCongelada(insumo.quantidade());
            item.setValorUnitario(insumo.valorUnitario());
            item.setLista(listas.get(insumo.id()));
            item.setSituacaoItem(SituacaoItem.NAO_CONFERIDO);

            itens.add(item);
        }
        return itens;
    }
}