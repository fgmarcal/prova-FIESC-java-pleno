package org.fiesc.felipe.backend.modules.service.external;

import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.modules.model.dto.EnderecoDto;
import org.springframework.stereotype.Service;
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
                return null;
            }

            return new EnderecoDto(
                    cep,
                    (String) response.get("logradouro"),
                    null,
                    (String) response.get("localidade"),
                    (String) response.get("uf")
            );
        } catch (Exception e) {
            log.error("Erro ao consultar CEP: {}", cep, e);
            return null;
        }
    }
}
