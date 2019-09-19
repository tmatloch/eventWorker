package pl.tmatloch.permutationworker.rabbitmq;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

import java.util.Map;

public class RabbitListenerFactoriesContainer {
    private final  Map<String, SimpleRabbitListenerContainerFactory> factoryMap;

    public RabbitListenerFactoriesContainer(Map<String, SimpleRabbitListenerContainerFactory> factoryMap) {
        this.factoryMap = factoryMap;
    }

    public Map<String, SimpleRabbitListenerContainerFactory> getFactoryMap() {
        return factoryMap;
    }
}
