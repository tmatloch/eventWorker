package pl.tmatloch.permutationworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PermutationWorkerConfig {

    @Bean
    public Queue queue() {
        return new Queue("permutation.rpc.requests");
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange("permutation.rpc");
    }

    @Bean
    public Binding binding(DirectExchange exchange,
                           Queue queue) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with("rpc");
    }

    @Bean
    public PermutationWorker permutationWorker(){
        return new PermutationWorker();
    }
}
