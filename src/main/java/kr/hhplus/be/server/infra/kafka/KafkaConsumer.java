package kr.hhplus.be.server.infra.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final String TOPIC = "hhplus-topic";
    private static final String GROUP_ID = "hhplus-group";

    @KafkaListener(topics = TOPIC, groupId = GROUP_ID)
    public void consumeMessage(String message) {
        System.out.println("Consumed message: " + message);
    }

}
