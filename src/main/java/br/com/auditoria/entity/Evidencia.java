package br.com.auditoria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "item_auditoria_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemAuditoria item;

    @Column(nullable = false)
    private String nomeOriginal;

    @Column(nullable = false, unique = true)
    private String arquivo;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private long tamanho;

    @Column(nullable = false)
    private LocalDateTime enviadaEm;
}
