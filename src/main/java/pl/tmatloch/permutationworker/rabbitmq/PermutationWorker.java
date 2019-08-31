package pl.tmatloch.permutationworker.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.List;

@Slf4j
public class PermutationWorker {


    @RabbitListener(queues = "permutation.rpc.requests")
    public List<String> permuteText(String text){
        log.info("worker - receive text = {}", text);
        int textLength = text.length();
        Permutation permutation = new Permutation();
        permutation.permute(text, 0, textLength - 1);
        log.info("worker - finished permutations result");
        return permutation.getPermutationResult();
    }
}
