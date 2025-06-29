package org.fiesc.felipe.backend.modules.infrastructure.queue.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.config.RabbitMQConfig;
import org.fiesc.felipe.backend.modules.shared.dto.PessoaIntegracaoStatusDto;
import org.fiesc.felipe.backend.modules.shared.dto.PessoaRequestDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PessoaIntegracaoProducer {

    private final RabbitTemplate rabbitTemplate;

    public void enviarPessoaParaFila(PessoaRequestDto dto) {
        log.info("Enviando pessoa para fila: {}", dto);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, dto);
    }

    public void enviarStatus(PessoaIntegracaoStatusDto statusDto) {
        log.info("Enviando status: {}", statusDto);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RETORNO_ROUTING_KEY, statusDto);
    }
}
