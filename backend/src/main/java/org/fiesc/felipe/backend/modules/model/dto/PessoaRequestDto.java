package org.fiesc.felipe.backend.modules.model.dto;


import java.io.Serializable;

public record PessoaRequestDto (
        String nome,
        String nascimento,
        String cpf,
        String email,
        EnderecoDto endereco
)implements Serializable {}
