package org.fiesc.felipe.api.modules.shared.dto;

import java.io.Serializable;

public record PessoaRequestDto(
        String nome,
        String nascimento,
        String cpf,
        String email,
        EnderecoDto endereco
) implements Serializable {}
