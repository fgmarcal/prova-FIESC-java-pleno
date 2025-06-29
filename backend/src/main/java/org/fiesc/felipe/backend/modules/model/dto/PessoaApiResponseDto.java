package org.fiesc.felipe.backend.modules.model.dto;

import java.io.Serializable;

public record PessoaApiResponseDto(
        String nome,
        String cpf,
        String nascimento,
        String email,
        EnderecoDto endereco,
        String dataHoraInclusao,
        String dataHoraAtualizacao,
        String status
) implements Serializable {}