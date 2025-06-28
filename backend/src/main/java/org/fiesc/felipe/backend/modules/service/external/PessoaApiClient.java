package org.fiesc.felipe.backend.modules.service.external;

import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PessoaApiClient {

    @Value("${api.pessoa.base-url}")
    private String apiBaseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public PessoaResponseDto criarPessoa(PessoaRequestDto dto) {
        String url = apiBaseUrl + "/pessoa/";
        return restTemplate.postForObject(url, dto, PessoaResponseDto.class);
    }

    public PessoaResponseDto atualizarPessoa(String cpf, PessoaRequestDto dto) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        restTemplate.put(url, dto);
        return new PessoaResponseDto(null, "Pessoa atualizada com sucesso");
    }

    public PessoaRequestDto consultarPessoaPorCpf(String cpf) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, PessoaRequestDto.class);
    }

    public List<PessoaRequestDto> listarTodas() {
        String url = apiBaseUrl + "/pessoa/all";
        PessoaRequestDto[] pessoas = restTemplate.getForObject(url, PessoaRequestDto[].class);
        return pessoas != null ? Arrays.asList(pessoas) : List.of();
    }

    public PessoaResponseDto removerPessoa(String cpf) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        restTemplate.delete(url);
        return new PessoaResponseDto(null, "Pessoa removida com sucesso");
    }
}
