package org.fiesc.felipe.backend.modules.infrastructure.external;

import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.modules.shared.exceptions.NotFoundException;
import org.fiesc.felipe.backend.modules.shared.dto.EnderecoDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Slf4j
public class CorreiosIntegrationServiceImpl implements CorreiosIntegrationService {

    private static final String VIACEP_URL = "https://viacep.com.br/ws/{cep}/json/";

    @Override
    public EnderecoDto buscarEnderecoPorCep(String cep) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(VIACEP_URL)
                    .buildAndExpand(Map.of("cep", cep))
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            if (response == null || response.containsKey("erro")) {
                throw new NotFoundException("CEP não encontrado: " + cep);
            }

            return new EnderecoDto(
                    cep,
                    (String) response.get("logradouro"),
                    null,
                    (String) response.get("localidade"),
                    (String) response.get("uf")
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.info(e.getMessage());
                throw new NotFoundException("CEP não encontrado: " + cep);
            }
            log.error("Erro HTTP ao consultar CEP: {}", cep, e);
            throw new NotFoundException("Erro ao consultar o CEP: " + cep, e);
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar CEP: {}", cep, e);
            throw new NotFoundException("Erro ao consultar o CEP: " + cep, e);
        }
    }
}
