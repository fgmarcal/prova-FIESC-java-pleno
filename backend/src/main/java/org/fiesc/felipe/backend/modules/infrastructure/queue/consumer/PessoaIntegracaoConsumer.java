package org.fiesc.felipe.backend.modules.infrastructure.queue.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.modules.shared.dto.PessoaRequestDto;
import org.fiesc.felipe.backend.modules.domain.service.interfaces.PessoaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PessoaIntegracaoConsumer {

    private final PessoaService pessoaService;

    @RabbitListener(queues = "fila.pessoa.integracao")
    @Transactional
    public void receberPessoa(PessoaRequestDto dto) {
        log.info("Consumindo pessoa da fila para integracao: {}", dto);
        pessoaService.integrarPessoa(dto);
    }
}
