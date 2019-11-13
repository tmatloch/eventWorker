package pl.tmatloch.eventworker.scaling;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.tmatloch.eventworker.rabbitmq.RabbitMessageListenerContainers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

@Component
public class ScalingComponent {

    private final Map<String, SimpleMessageListenerContainer> containerMap;

    private final Map<String, Double> currentScalePercentage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> currentThreadCount = new ConcurrentHashMap<>();

    private final Map<String, Gauge> gaugeMap = new HashMap<>();

    private final Integer maxConcurrentThreads;
    private final MeterRegistry meterRegistry;


    @Autowired
    public ScalingComponent(RabbitMessageListenerContainers factoryContainer, MeterRegistry meterRegistry, @Value("${rabbitmq.scale.maxConcurrentThreads:10}") Integer maxConcurrentThreads) {
        this.containerMap = factoryContainer.getFactoryMap();
        this.maxConcurrentThreads = maxConcurrentThreads;
        this.meterRegistry = meterRegistry;
    }

    private Gauge createGauge(String key, AtomicInteger keyThreadCount, MeterRegistry meterRegistry) {
        return Gauge.builder(key + "_event_threads", keyThreadCount::get).register(meterRegistry);
    }

    public void init() {
        int size = containerMap.size();
        double initPercentage = (double) 1 / size;
        containerMap.keySet().forEach(name -> currentScalePercentage.put(name, initPercentage));
        scale(currentScalePercentage);
    }

    public Map<String, Double> getCurrentScalePercentage() {
        return currentScalePercentage;
    }

    public Map<String, Double> scaleByWeight(Map<String, Double> scaleNewWeight){
        if(currentScalePercentage.keySet().stream().allMatch(scaleNewWeight::containsKey)){
            long weightSum = Math.round(scaleNewWeight.values().stream().mapToDouble(Double::doubleValue).sum());
            if (weightSum != 1) {
                throw new IllegalArgumentException("Sum of percentage must be equal 1");
            }
            currentScalePercentage.replaceAll((key, value) -> scaleNewWeight.get(key));
            scale(currentScalePercentage);
        }
        return currentScalePercentage;
    }

    private void scale(Map<String, Double> newScalePercentage) {
        Map<String, Integer> calculatedThreads = calculateConcurentThreads(newScalePercentage);
        calculatedThreads.forEach((key, value) -> {
            SimpleMessageListenerContainer container = containerMap.get(key);
            container.setConcurrentConsumers(value);
            currentThreadCount.computeIfAbsent(key, (mapKey) -> {
                AtomicInteger threadCount = new AtomicInteger(0);
                gaugeMap.put(key, createGauge(key, threadCount,meterRegistry));
                return threadCount;
            }).getAndSet(value);
        });

    }

    private Map<String, Integer> calculateConcurentThreads(Map<String, Double> newScalePercentage) {

        Map<String, Double> notRoundedThreadCount = newScalePercentage.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue() * maxConcurrentThreads));
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
                return Double.compare(fractT1, fractADouble);
            })).forEachOrdered(e -> e.setValue(e.getValue() + intSupplier.getAsInt()));
        }
        return notRoundedThreadCount.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().intValue()));
    }


}
