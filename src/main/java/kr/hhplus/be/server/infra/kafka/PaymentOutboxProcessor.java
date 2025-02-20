package kr.hhplus.be.server.infra.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.payment.entity.PaymentOutboxEntity;
import kr.hhplus.be.server.domain.payment.repository.PaymentOutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentOutboxProcessor {
    private final KafkaProducer kafkaProducer;
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentOutboxProcessor(KafkaProducer kafkaProducer, PaymentOutboxRepository paymentOutboxRepository) {
        this.kafkaProducer = kafkaProducer;
        this.paymentOutboxRepository = paymentOutboxRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void processOutbox() {
        List<PaymentOutboxEntity> events = paymentOutboxRepository.findByProcessedFalse();

        for (PaymentOutboxEntity event : events) {
            kafkaProducer.sendMessage(event.getPayload());
            event.setProcessed(true);
            paymentOutboxRepository.save(event);
        }
    }
}
