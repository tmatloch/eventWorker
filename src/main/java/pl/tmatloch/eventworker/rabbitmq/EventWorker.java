package pl.tmatloch.eventworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.tmatloch.eventworker.permutation.Permutation;
import pl.tmatloch.eventworker.permutation.PermutationIterate;


import java.time.Instant;
import java.util.List;

@Slf4j
public class EventWorker {


    @RabbitListener(queues = "fast.event.rpc.requests", containerFactory = "fastRabbitFactory")
    public EventMessage permuteFastText(EventMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        permutation.calculatePermutations(message.getProcessData());
        Instant afterProcess = Instant.now();
        log.info("worker - finished events result");
        message.setResult(permutation.getPermutationResults());
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }

    @RabbitListener(queues = "slow.event.rpc.requests",  containerFactory = "slowRabbitFactory")
    public EventMessage permuteSlowText(EventMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        permutation.calculatePermutations(message.getProcessData());
        Instant afterProcess = Instant.now();
        log.info("worker - finished events result");
        message.setResult(permutation.getPermutationResults());
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }



}