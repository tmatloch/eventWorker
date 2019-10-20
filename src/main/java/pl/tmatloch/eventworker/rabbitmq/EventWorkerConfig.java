package pl.tmatloch.eventworker.rabbitmq;

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
import pl.tmatloch.eventworker.scaling.ScalingComponent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class EventWorkerConfig {

    Map<String, SimpleMessageListenerContainer> containerMap = new HashMap<>();

    @Bean("slowQueue")
    public Queue slowQueue() {
        return new Queue("slow.event.rpc.requests");
    }

    @Bean("fastQueue")
    public Queue fastQueue() {
        return new Queue("fast.event.rpc.requests");
    }

    @Bean("slowEventExchange")
    public DirectExchange slowEventExchange() {
        return new DirectExchange("slow.event.rpc");
    }

    @Bean("fastEventExchange")
    public DirectExchange fastEventExchange() {
        return new DirectExchange("fast.event.rpc");
    }

    @Bean
    public Binding slowBinding(@Qualifier("slowEventExchange") DirectExchange exchange,
                               @Qualifier("slowQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public Binding fastBinding(@Qualifier("fastEventExchange") DirectExchange exchange,
                               @Qualifier("fastQueue") Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public EventWorker permutationWorker() {
        return new EventWorker();
    }

    @Bean
    MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean("slowRabbitFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> slowRabbitListenerContainerFactory(
            ConnectionFactory rabbitConnectionFactory, ScalingComponent scalingComponent) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory() {

            @Override
            protected SimpleMessageListenerContainer createContainerInstance(){
                SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
                containerMap.put("slow", container);
                scalingComponent.init();
                return container;
            }
        };
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean("fastRabbitFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> fastRabbitListenerContainerFactory(
            ConnectionFactory rabbitConnectionFactory, ScalingComponent scalingComponent) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory() {
            @Override
            protected SimpleMessageListenerContainer createContainerInstance(){
                SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
                containerMap.put("fast", container);
                scalingComponent.init();
                return container;
            }
        };
        factory.setMessageConverter(jackson2JsonMessageConverter());
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean
    public RabbitMessageListenerContainers factoriesContainer() {
        return new RabbitMessageListenerContainers(containerMap);
    }

}