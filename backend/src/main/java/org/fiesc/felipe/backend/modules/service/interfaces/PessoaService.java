package org.fiesc.felipe.backend.modules.service.interfaces;

import org.fiesc.felipe.backend.modules.model.dto.PessoaApiResponseDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaResponseDto;
import org.fiesc.felipe.backend.modules.model.dto.ResponseDto;

import java.util.List;

public interface PessoaService {
    PessoaResponseDto criar(PessoaRequestDto dto);
    PessoaResponseDto atualizar(String cpf, PessoaRequestDto dto);
    void remover(String cpf);
    List<PessoaApiResponseDto> listarTodos();
    PessoaApiResponseDto consultarPorCpf(String cpf);
    ResponseDto reenviarIntegracao(String cpf);
    ResponseDto integrarPessoa(PessoaRequestDto dto);
}
