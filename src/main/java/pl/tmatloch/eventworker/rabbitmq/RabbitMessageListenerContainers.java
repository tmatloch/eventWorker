package pl.tmatloch.eventworker.rabbitmq;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import java.util.Map;

public class RabbitMessageListenerContainers {
    private final  Map<String, SimpleMessageListenerContainer> factoryMap;

    public RabbitMessageListenerContainers(Map<String, SimpleMessageListenerContainer> factoryMap) {
        this.factoryMap = factoryMap;
    }

    public Map<String, SimpleMessageListenerContainer> getFactoryMap() {
        return factoryMap;
    }
}
