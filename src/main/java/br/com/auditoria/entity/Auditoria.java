package br.com.auditoria.entity;

import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.SituacaoAuditoria;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String obraId;

    @Column(nullable = false)
    private String obraNome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Segmento segmento;

    @Column(nullable = false)
    private String filialId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SituacaoAuditoria situacaoAuditoria;

    @Column(nullable = false)
    private LocalDateTime criadaEm;

    @Column(nullable = false)
    private LocalDateTime referenciaEm;

    private LocalDateTime concluidaEm;

    @Column(nullable = false)
    private String responsavel;

    private String filialNome;
}
