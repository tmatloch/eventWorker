package pl.tmatloch.permutationworker.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class PermutationWorkerConfig {

    private Map<String, SimpleRabbitListenerContainerFactory> factoryMap = new HashMap<>();

    @Bean("slowQueue")
    public Queue slowQueue() {
        return new Queue("slow.permutation.rpc.requests");
    }

    @Bean("fastQueue")
    public Queue fastQueue() {
        return new Queue("fast.permutation.rpc.requests");
    }

    @Bean("slowPermutationExchange")
    public DirectExchange slowPermutationExchange() {
        return new DirectExchange("slow.permutation.rpc");
    }

    @Bean("fastPermutationExchange")
    public DirectExchange fastPermutationExchange() {
        return new DirectExchange("fast.permutation.rpc");
    }

    @Bean
    public Binding slowBinding(@Qualifier("slowPermutationExchange") DirectExchange exchange,
                               @Qualifier("slowQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public Binding fastBinding(@Qualifier("fastPermutationExchange") DirectExchange exchange,
                               @Qualifier("fastQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public PermutationWorker permutationWorker() {
        return new PermutationWorker();
    }

    @Bean
    MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean("slowRabbitFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> slowRabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(10);
        factoryMap.put("slow", factory);
        return factory;
    }

    @Bean("fastRabbitFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> fastRabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(10);
        factoryMap.put("fast", factory);
        return factory;
    }

    @Bean
    @DependsOn({"fastRabbitFactory", "slowRabbitFactory"})
    public RabbitListenerFactoriesContainer factoriesContainer() {
        return new RabbitListenerFactoriesContainer(factoryMap);
    }

}