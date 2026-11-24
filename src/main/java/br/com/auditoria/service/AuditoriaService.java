package br.com.auditoria.service;

import br.com.auditoria.dto.auditoria.*;
import br.com.auditoria.dto.estoque.InsumoEstoqueDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.entity.Auditoria;
import br.com.auditoria.entity.ItemAuditoria;
import br.com.auditoria.entity.Comunicado;
import br.com.auditoria.entity.ItemForaEstoque;
import br.com.auditoria.enums.SituacaoAuditoria;
import br.com.auditoria.enums.SituacaoItem;
import br.com.auditoria.exception.ConflitoException;
import br.com.auditoria.repository.AuditoriaRepository;
import br.com.auditoria.repository.ResponsavelRepository;
import br.com.auditoria.repository.ItemAuditoriaRepository;
import br.com.auditoria.repository.ComunicadoRepository;
import br.com.auditoria.repository.ItemForaEstoqueRepository;
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
    private final ItemForaEstoqueRepository itemForaEstoqueRepository;
    private final ComunicadoRepository comunicadoRepository;
    private final ResponsavelRepository responsavelRepository;

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
        List<InsumoEstoqueDto> daObra = estoqueService.lerParaCongelar(obra, requestDto.obraId());

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

    @Transactional
    public AuditoriaDetalheDto adicionarForaDoEstoque(Long auditoriaId,
                                                      ItemForaEstoqueRequestDto requestDto) {
        Auditoria auditoria = exigirAuditoria(auditoriaId);
        exigirEmAndamento(auditoria);

        ItemForaEstoque item = new ItemForaEstoque();
        item.setAuditoria(auditoria);
        preencherForaDoEstoque(requestDto, item);
        itemForaEstoqueRepository.save(item);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(auditoriaId));
    }

    @Transactional
    public AuditoriaDetalheDto atualizarForaDoEstoque(Long auditoriaId, Long itemId,
                                                      ItemForaEstoqueRequestDto requestDto) {
        Auditoria auditoria = exigirAuditoria(auditoriaId);
        exigirEmAndamento(auditoria);

        ItemForaEstoque item = exigirForaDoEstoque(auditoriaId, itemId);
        preencherForaDoEstoque(requestDto, item);
        itemForaEstoqueRepository.save(item);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(auditoriaId));
    }

    @Transactional
    public AuditoriaDetalheDto removerForaDoEstoque(Long auditoriaId, Long itemId) {
        Auditoria auditoria = exigirAuditoria(auditoriaId);
        exigirEmAndamento(auditoria);

        itemForaEstoqueRepository.delete(exigirForaDoEstoque(auditoriaId, itemId));

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(auditoriaId));
    }

    private ItemForaEstoque exigirForaDoEstoque(Long auditoriaId, Long itemId) {
        ItemForaEstoque item = itemForaEstoqueRepository.findById(itemId)
                .orElseThrow(() -> new NoSuchElementException("Item nao encontrado: " + itemId));

        if (!item.getAuditoria().getId().equals(auditoriaId)) {
            throw new NoSuchElementException("Item nao encontrado: " + itemId);
        }

        return item;
    }

    private void preencherForaDoEstoque(ItemForaEstoqueRequestDto requestDto,
                                        ItemForaEstoque item) {
        item.setDescricao(requestDto.descricao().trim());
        item.setCodigo(textoOuNulo(requestDto.codigo()));
        item.setUnidade(requestDto.unidade().trim());
        item.setQuantidade(requestDto.quantidade());
        item.setValorUnitario(requestDto.valorUnitario());
        item.setLocal(textoOuNulo(requestDto.local()));
        item.setObservacao(requestDto.observacao() == null ? "" : requestDto.observacao().trim());
    }

    private static String textoOuNulo(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    @Transactional
    public AuditoriaDetalheDto salvarComunicado(Long auditoriaId, ComunicadoRequestDto requestDto) {
        Auditoria auditoria = exigirAuditoria(auditoriaId);
        exigirNaoCancelada(auditoria);
        exigirDestinatariosConhecidos(requestDto.destinatariosIds());

        Comunicado comunicado = comunicadoRepository.findByAuditoriaId(auditoriaId)
                .orElseGet(Comunicado::new);
        comunicado.setAuditoria(auditoria);
        comunicado.getDestinatariosIds().clear();
        comunicado.getDestinatariosIds().addAll(requestDto.destinatariosIds());
        comunicado.setPrazo(requestDto.prazo());
        comunicado.setAssunto(requestDto.assunto().trim());
        comunicado.setMensagem(requestDto.mensagem());
        comunicado.setGeradoEm(LocalDateTime.now());
        comunicadoRepository.save(comunicado);

        return paraDetalhe(auditoria, itemAuditoriaRepository.findByAuditoriaId(auditoriaId));
    }

    private void exigirNaoCancelada(Auditoria auditoria) {
        if (auditoria.getSituacaoAuditoria() == SituacaoAuditoria.CANCELADA) {
            throw new ConflitoException(
                    "A auditoria " + auditoria.getId() + " foi cancelada.");
        }
    }

    private void exigirDestinatariosConhecidos(List<String> ids) {
        for (String id : ids) {
            long numero;

            try {
                numero = Long.parseLong(id.trim());
            } catch (NumberFormatException naoEhNumero) {
                throw new IllegalArgumentException("Destinatario invalido: " + id);
            }

            if (!responsavelRepository.existsById(numero)) {
                throw new IllegalArgumentException("Destinatario nao cadastrado: " + id);
            }
        }
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
                auditoria.getObraNome(), auditoria.getSegmento(), auditoria.getFilialNome(),
                auditoria.getResponsavel(),
                auditoria.getSituacaoAuditoria(), auditoria.getCriadaEm(), auditoria.getConcluidaEm(),
                referencia, itensDto, foraDoEstoqueDe(auditoria), comunicadoDe(auditoria));
    }

    private ComunicadoDto comunicadoDe(Auditoria auditoria) {
        return comunicadoRepository.findByAuditoriaId(auditoria.getId())
                .map(comunicado -> new ComunicadoDto(List.copyOf(comunicado.getDestinatariosIds()),
                        comunicado.getPrazo(), comunicado.getAssunto(), comunicado.getMensagem(),
                        comunicado.getGeradoEm()))
                .orElse(null);
    }

    private List<ItemForaEstoqueDto> foraDoEstoqueDe(Auditoria auditoria) {
        return itemForaEstoqueRepository.findByAuditoriaId(auditoria.getId())
                .stream().map(this::paraForaDto).toList();
    }

    private ItemForaEstoqueDto paraForaDto(ItemForaEstoque item) {
        return new ItemForaEstoqueDto(String.valueOf(item.getId()), item.getDescricao(),
                item.getCodigo(), item.getUnidade(), item.getQuantidade(),
                item.getValorUnitario(), item.getLocal(),
                item.getObservacao() == null ? "" : item.getObservacao());
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