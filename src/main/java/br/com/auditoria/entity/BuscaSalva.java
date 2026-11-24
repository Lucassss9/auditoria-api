package br.com.auditoria.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuscaSalva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "busca_salva_termo",
            joinColumns = @JoinColumn(name = "busca_salva_id"))
    @Column(name = "termo", nullable = false)
    private List<String> termos = new ArrayList<>();
}
