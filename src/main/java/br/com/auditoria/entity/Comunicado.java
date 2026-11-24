package br.com.auditoria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_comunicado_auditoria",
        columnNames = "auditoria_id"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "auditoria_id", nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Auditoria auditoria;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comunicado_destinatario",
            joinColumns = @JoinColumn(name = "comunicado_id"))
    @Column(name = "responsavel_id", nullable = false)
    private Set<String> destinatariosIds = new LinkedHashSet<>();

    @Column(nullable = false)
    private LocalDate prazo;

    @Column(nullable = false)
    private String assunto;

    @Column(nullable = false, length = 8000)
    private String mensagem;

    @Column(nullable = false)
    private LocalDateTime geradoEm;
}
