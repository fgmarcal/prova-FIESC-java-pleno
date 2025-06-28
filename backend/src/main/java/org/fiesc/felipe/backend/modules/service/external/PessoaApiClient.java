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


    public PessoaRequestDto consultarPessoaPorCpf(String cpf) {
        String url = apiBaseUrl + "/pessoa/cpf/" + cpf;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, PessoaRequestDto.class);
    }

    public List<PessoaRequestDto> listarTodas() {
        PessoaRequestDto[] pessoas = restTemplate.getForObject(apiBaseUrl, PessoaRequestDto[].class);
        return pessoas != null ? Arrays.asList(pessoas) : List.of();
    }

    public PessoaResponseDto removerPessoa(String cpf) {
        restTemplate.delete(apiBaseUrl + "/cpf/" + cpf);
        return new PessoaResponseDto(null, "Pessoa removida com sucesso");
    }
}
