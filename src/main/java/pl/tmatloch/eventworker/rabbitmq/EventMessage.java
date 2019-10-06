package pl.tmatloch.eventworker.rabbitmq;

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
public class EventMessage {

    String id;
    Instant onCreateTime;
    Instant onStartProcess;
    Instant onEndProcess;

    String processData;
    List<String> result;

    public static EventMessage create(String dataToProcess) {
        String id = UUID.randomUUID().toString();
        Instant instant = Instant.now();
        return new EventMessage(id, instant, null, null, dataToProcess, null);
    }
}