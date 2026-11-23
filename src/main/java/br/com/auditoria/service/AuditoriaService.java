package br.com.auditoria.service;

import br.com.auditoria.client.CfObrasClient;
import br.com.auditoria.dto.auditoria.*;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.entity.Auditoria;
import br.com.auditoria.entity.ItemAuditoria;
import br.com.auditoria.enums.SituacaoAuditoria;
import br.com.auditoria.enums.SituacaoItem;
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