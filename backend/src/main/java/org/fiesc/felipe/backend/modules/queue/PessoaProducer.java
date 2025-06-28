package org.fiesc.felipe.backend.modules.queue;

import lombok.RequiredArgsConstructor;
import org.fiesc.felipe.backend.config.RabbitMQConfig;
import org.fiesc.felipe.backend.modules.model.dto.PessoaRequestDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PessoaProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarPessoaParaFila(PessoaRequestDto dto) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
    }
}
