package org.fiesc.felipe.backend.modules.queue.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiesc.felipe.backend.modules.model.dto.PessoaIntegracaoStatusDto;
import org.fiesc.felipe.backend.modules.repository.PessoaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PessoaIntegracaoConsumer {

    private final PessoaRepository pessoaRepository;

    @RabbitListener(queues = "fila.pessoa.retorno")
    @Transactional
    public void receberStatus(PessoaIntegracaoStatusDto dto) {
        log.info("Recebido status de integração: {}", dto);

        pessoaRepository.findByCpf(dto.cpf()).ifPresentOrElse(pessoa -> {
            pessoa.setSituacaoIntegracao(dto.situacao());
            pessoaRepository.save(pessoa);
            log.info("Pessoa atualizada com status: {}", dto.situacao());
        }, () -> {
            log.warn("Pessoa com CPF {} não encontrada para atualização de status", dto.cpf());
        });
    }
}
