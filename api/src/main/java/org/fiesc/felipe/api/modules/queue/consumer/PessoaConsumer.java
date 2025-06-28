package org.fiesc.felipe.api.modules.queue.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.api.modules.model.dto.PessoaRequestDto;
import org.fiesc.felipe.api.modules.service.interfaces.PessoaService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PessoaConsumer {

    private final PessoaService pessoaService;

    @RabbitListener(queues = "fila.pessoa.integracao")
    public void receberPessoa(PessoaRequestDto dto) {
        log.info("Recebendo pessoa da fila: {}", dto);
        pessoaService.salvarPessoa(dto);
    }
}
