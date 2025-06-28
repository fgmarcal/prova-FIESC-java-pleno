package org.fiesc.felipe.api.modules.service.interfaces;

import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaResponseDto;

import java.util.List;

public interface PessoaService {
    void salvarPessoa(PessoaRequestDto dto);
    List<PessoaRequestDto> listarTodos();
    PessoaRequestDto consultarPorCpf(String cpf);
    PessoaResponseDto removerPorCpf(String cpf);
    void atualizarPessoa(PessoaRequestDto dto);
    boolean existePorCpf(String cpf);
}
