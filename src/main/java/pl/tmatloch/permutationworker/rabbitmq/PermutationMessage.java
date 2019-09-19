package pl.tmatloch.permutationworker.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PermutationMessage {

    String id;
    Instant onCreateTime;
    Instant onStartProcess;
    Instant onEndProcess;

    String processData;
    List<String> result;

    public static PermutationMessage create(String dataToProcess) {
        String id = UUID.randomUUID().toString();
        Instant instant = Instant.now();
        return new PermutationMessage(id, instant, null, null, dataToProcess, null);
    }
}
