package org.fiesc.felipe.api.modules.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "enderecos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cep;
    private String rua;
    private Integer numero;
    private String cidade;
    private String estado;

    @OneToOne(mappedBy = "endereco")
    private Pessoa pessoa;
}
