package br.com.auditoria.entity;

import br.com.auditoria.enums.FuncaoObra;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_responsavel_email",
        columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "responsavel_obra",
            joinColumns = @JoinColumn(name = "responsavel_id"))
    @Column(name = "obra_id", nullable = false)
    private Set<String> obraIds = new LinkedHashSet<>();

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FuncaoObra funcaoObra;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean ativo;

    @Column(nullable = false)
    private boolean receberEmail;
}
