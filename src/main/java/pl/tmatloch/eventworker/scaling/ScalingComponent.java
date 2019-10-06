package pl.tmatloch.eventworker.scaling;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.tmatloch.eventworker.rabbitmq.RabbitMessageListenerContainers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

@Component
public class ScalingComponent {

    private final Map<String, SimpleMessageListenerContainer> contaierMap;

    private final Map<String, Double> currentScalePercentage = new ConcurrentHashMap<>();

    private final Integer maxConcurrentThreads;

    @Autowired
    public ScalingComponent(RabbitMessageListenerContainers factoryContainer, @Value("${rabbitmq.scale.maxConcurrentThreads:10}") Integer maxConcurrentThreads) {
        this.contaierMap = factoryContainer.getFactoryMap();
        this.maxConcurrentThreads = maxConcurrentThreads;
    }

    public void init() {
        int size = contaierMap.size();
        double initPercentage = (double) 1 / size;
        contaierMap.keySet().forEach(name -> currentScalePercentage.put(name, initPercentage));
        scale(currentScalePercentage);
    }

    public Map<String, Double> getCurrentScalePercentage() {
        return currentScalePercentage;
    }

    public Map<String, Double> scaleByWeight(Map<String, Double> scaleNewWeight){
        if(currentScalePercentage.keySet().stream().allMatch(scaleNewWeight::containsKey)){
            long weightSum = Math.round(scaleNewWeight.values().stream().mapToDouble(Double::doubleValue).sum());
            if (weightSum != 1) {
                //TODO recalculateWetights
            }
            currentScalePercentage.replaceAll((key, value) -> scaleNewWeight.get(key));
            scale(currentScalePercentage);
        }
        return currentScalePercentage;
    }

    private void scale(Map<String, Double> newScalePercentage) {
        Map<String, Integer> calculatedThreads = calculateConcurentThreads(newScalePercentage);
        calculatedThreads.entrySet().forEach((entry) -> {
            SimpleMessageListenerContainer container = contaierMap.get(entry.getKey());
            container.setConcurrentConsumers(entry.getValue());
        });

    }

    private Map<String, Integer> calculateConcurentThreads(Map<String, Double> newScalePercentage) {

        Map<String, Double> notRoundedThreadCount = newScalePercentage.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue() * maxConcurrentThreads));
        final int missingThreadCount = maxConcurrentThreads - notRoundedThreadCount.values().stream().mapToInt(Double::intValue).sum();
        if(missingThreadCount > 0){

            IntSupplier intSupplier = new IntSupplier() {

                AtomicInteger supply = new AtomicInteger(missingThreadCount);
                @Override
                public int getAsInt() {
                    int value = supply.getAndDecrement();
                    return Math.max(value, 0);
                }
            };

            notRoundedThreadCount.entrySet().stream().sorted(Map.Entry.comparingByValue((aDouble, t1) -> {
                Double fractADouble = (aDouble - aDouble.intValue());
                Double fractT1 = (t1 - t1.intValue());
                return Double.compare(fractADouble, fractT1);
            })).forEachOrdered(e -> e.setValue(e.getValue() + intSupplier.getAsInt()));
        }
        return notRoundedThreadCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().intValue()));
    }


}
