package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.domain.payment.dto.PaymentConfirmedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaymentEventListener {
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentConfirmedEvent(PaymentConfirmedEvent event) {
        System.out.println("결제 정보 전송: " + event);
    }
}
