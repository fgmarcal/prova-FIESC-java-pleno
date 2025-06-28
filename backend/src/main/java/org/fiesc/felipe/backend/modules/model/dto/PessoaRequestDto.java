package org.fiesc.felipe.backend.modules.model.dto;


public record PessoaRequestDto (
        String nome,
        String dataNascimento,
        String cpf,
        String email,
        EnderecoDto endereco
){}
