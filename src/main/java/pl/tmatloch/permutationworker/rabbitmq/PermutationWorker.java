package pl.tmatloch.permutationworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.tmatloch.permutationworker.permutation.Permutation;
import pl.tmatloch.permutationworker.permutation.PermutationIterate;


import java.time.Instant;
import java.util.List;

@Slf4j
public class PermutationWorker {


    @RabbitListener(queues = "fast.permutation.rpc.requests", containerFactory = "fastRabbitFactory")
    public PermutationMessage permuteFastText(PermutationMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        permutation.calculatePermutations(message.getProcessData());
        Instant afterProcess = Instant.now();
        log.info("worker - finished permutations result");
        message.setResult(permutation.getPermutationResults());
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }

    @RabbitListener(queues = "slow.permutation.rpc.requests",  containerFactory = "slowRabbitFactory")
    public PermutationMessage permuteSlowText(PermutationMessage message){
        Instant beforeProcess = Instant.now();
        log.info("worker - receive text = {}", message.getProcessData());
        Permutation permutation = new PermutationIterate();
        permutation.calculatePermutations(message.getProcessData());
        Instant afterProcess = Instant.now();
        log.info("worker - finished permutations result");
        message.setResult(permutation.getPermutationResults());
        message.setOnStartProcess(beforeProcess);
        message.setOnEndProcess(afterProcess);
        return message;
    }



}