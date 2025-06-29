package org.fiesc.felipe.backend.modules.service.external;

import org.fiesc.felipe.backend.modules.model.dto.PessoaApiResponseDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.model.dto.PessoaResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PessoaApiClient {

    @Value("${api.pessoa.base-url}")
    private String apiBaseUrl;
    private final RestTemplate restTemplate;

    public PessoaApiClient(@Value("${api.pessoa.base-url}") String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
        this.restTemplate = new RestTemplate();
        this.restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }

    public PessoaResponseDto criarPessoa(PessoaRequestDto dto) {
        String url = apiBaseUrl + "/pessoa/";
        return restTemplate.postForObject(url, dto, PessoaResponseDto.class);
    }

    public PessoaResponseDto atualizarPessoa(String cpf, PessoaRequestDto dto) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        restTemplate.put(url, dto);
        return new PessoaResponseDto(null, "Pessoa atualizada com sucesso");
    }

    public PessoaApiResponseDto consultarPessoaPorCpf(String cpf) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, PessoaApiResponseDto.class);
    }

    public List<PessoaApiResponseDto> listarTodas() {
        String url = apiBaseUrl + "/pessoa/all";
        PessoaApiResponseDto[] pessoas = restTemplate.getForObject(url, PessoaApiResponseDto[].class);
        return pessoas != null ? Arrays.asList(pessoas) : List.of();
    }

    public PessoaResponseDto removerPessoa(String cpf) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;

        return restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                PessoaResponseDto.class
        ).getBody();
    }
}
