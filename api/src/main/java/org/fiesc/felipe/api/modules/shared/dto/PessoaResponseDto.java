package org.fiesc.felipe.api.modules.shared.dto;

import java.io.Serializable;

public record PessoaResponseDto(
        String nome,
        String cpf,
        String nascimento,
        String email,
        EnderecoDto endereco,
        String dataHoraInclusao,
        String dataHoraAtualizacao
) implements Serializable {}
