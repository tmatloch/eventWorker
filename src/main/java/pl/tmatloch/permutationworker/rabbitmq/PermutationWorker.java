package pl.tmatloch.permutationworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import pl.tmatloch.permutationworker.permutation.Permutation;
import pl.tmatloch.permutationworker.permutation.PermutationIterate;


import java.util.List;

@Slf4j
public class PermutationWorker {


    @RabbitListener(queues = "permutation.rpc.requests")
    public List<String> permuteText(String text){
        log.info("worker - receive text = {}", text);
        Permutation permutation = new PermutationIterate();
        permutation.calculatePermutations(text);
        log.info("worker - finished permutations result");
        return permutation.getPermutationResults();
    }

}