package org.fiesc.felipe.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fiesc.felipe.backend.modules.shared.dto.PessoaIntegracaoStatusDto;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "fila.pessoa.integracao";
    public static final String EXCHANGE = "pessoa.exchange";
    public static final String ROUTING_KEY = "pessoa.routingkey";

    public static final String RETORNO_QUEUE = "fila.pessoa.retorno";
    public static final String RETORNO_ROUTING_KEY = "pessoa.retorno";

    @Bean
    public Queue integracaoQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Queue retornoQueue() {
        return new Queue(RETORNO_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding integracaoBinding(Queue integracaoQueue, DirectExchange exchange) {
        return BindingBuilder.bind(integracaoQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding retornoBinding(Queue retornoQueue, DirectExchange exchange) {
        return BindingBuilder.bind(retornoQueue).to(exchange).with(RETORNO_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("*");
        typeMapper.setIdClassMapping(Map.of(
                PessoaIntegracaoStatusDto.class.getName(), PessoaIntegracaoStatusDto.class
        ));

        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
