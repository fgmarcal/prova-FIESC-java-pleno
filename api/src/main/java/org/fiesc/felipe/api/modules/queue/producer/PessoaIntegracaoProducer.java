package org.fiesc.felipe.api.modules.queue.producer;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.api.modules.model.dto.PessoaIntegracaoStatusDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PessoaIntegracaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarStatus(PessoaIntegracaoStatusDto dto) {
        rabbitTemplate.convertAndSend("pessoa.exchange", "pessoa.retorno", dto);
    }
}
