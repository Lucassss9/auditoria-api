package br.com.auditoria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemForaEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "auditoria_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Auditoria auditoria;

    @Column(nullable = false)
    private String descricao;

    private String codigo;

    @Column(nullable = false)
    private String unidade;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidade;

    @Column(precision = 15, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "local_item")
    private String local;

    private String observacao;
}
