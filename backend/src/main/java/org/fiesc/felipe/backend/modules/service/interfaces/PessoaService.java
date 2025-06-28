package org.fiesc.felipe.backend.modules.service.interfaces;

import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaResponseDto;

import java.util.List;

public interface PessoaService {
    PessoaResponseDto salvar(PessoaRequestDto dto);
    PessoaResponseDto atualizar(String cpf, PessoaRequestDto dto);
    void remover(String cpf);
    List<PessoaRequestDto> listarTodos();
    PessoaRequestDto consultarPorCpf(String cpf);
    void reenviarIntegracao(String cpf);
    void integrarPessoa(PessoaRequestDto dto);
}
