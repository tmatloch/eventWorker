package pl.tmatloch.eventworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.tmatloch.eventworker.permutation.Permutation;
import pl.tmatloch.eventworker.permutation.PermutationIterate;


import java.time.Instant;

@Slf4j
public class EventWorker {


    @RabbitListener(queues = "fast.event.rpc.requests", containerFactory = "fastRabbitFactory")
    public EventMessage permuteFastText(EventMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker fast - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        int multiply = message.getMultiply();
        for (int i = 1; i <= multiply; i++) {
            log.info("calculating {} of {}", i, multiply);
            permutation.calculatePermutations(message.getProcessData());
        }
        Instant afterProcess = Instant.now();
        log.info("worker fast - finished events result");
        message.setResultStatus("OK");
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }

    @RabbitListener(queues = "slow.event.rpc.requests",  containerFactory = "slowRabbitFactory")
    public EventMessage permuteSlowText(EventMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker slow - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        int multiply = message.getMultiply();
        for (int i = 1; i <= multiply; i++) {
            log.info("calculating {} of {}", i, multiply);
            permutation.calculatePermutations(message.getProcessData());
        }
        Instant afterProcess = Instant.now();
        log.info("worker slow - finished events result");
        message.setResultStatus("OK");
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }



}