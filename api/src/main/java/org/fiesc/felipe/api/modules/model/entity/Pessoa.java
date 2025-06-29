package org.fiesc.felipe.api.modules.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pessoas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPessoa;

    private String nome;

    private LocalDate nascimento;

    @Column(unique = true, nullable = false)
    private String cpf;

    private String email;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_endereco")
    private Endereco endereco;

    private LocalDateTime dataHoraInclusaoRegistro;

    private LocalDateTime dataHoraUltimaAlteracaoRegistro;

    @PrePersist
    public void aoSalvar() {
        this.dataHoraInclusaoRegistro = LocalDateTime.now();
        this.dataHoraUltimaAlteracaoRegistro = LocalDateTime.now();
    }

    @PreUpdate
    public void aoAtualizar() {
        this.dataHoraUltimaAlteracaoRegistro = LocalDateTime.now();
    }
}
