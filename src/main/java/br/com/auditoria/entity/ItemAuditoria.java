package br.com.auditoria.entity;

import br.com.auditoria.enums.SituacaoItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity()
@Table()
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "auditoria_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Auditoria auditoria;

    @Column(nullable = false)
    private String insumoId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidadeCongelada;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorUnitario;

    @Column(precision = 15, scale = 3)
    private BigDecimal quantidadeContada;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SituacaoItem situacaoItem;

    private String motivo;

    private String observacao;

}
