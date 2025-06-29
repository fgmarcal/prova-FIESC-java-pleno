package org.fiesc.felipe.api.modules.service.interfaces;

import org.fiesc.felipe.api.modules.exceptions.NotFoundException;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.model.dto.PessoaResponseDto;

import java.util.List;

public interface PessoaService {
    void criarPessoa(PessoaRequestDto dto);
    List<PessoaRequestDto> listarTodos();
    PessoaRequestDto consultarPorCpf(String cpf) throws NotFoundException;
    PessoaResponseDto removerPorCpf(String cpf) throws NotFoundException;
    void atualizarPessoa(PessoaRequestDto dto) throws NotFoundException;
}
