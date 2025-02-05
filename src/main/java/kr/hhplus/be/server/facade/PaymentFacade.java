package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.payment.dto.PaymentServiceDto;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentFacade {
    private final PaymentService paymentService;

    public PaymentFacade(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentServiceDto createPayment(Long userId, Long orderId, Long couponId) {
        return paymentService.createPayment(userId, orderId, couponId);
    }

    public PaymentServiceDto confirmPayment(Long userId, Long paymentId) {
        return paymentService.confirmPayment(userId, paymentId);
    }

    public PaymentServiceDto cancelPayment(Long userId, Long paymentId) {
        return paymentService.cancelPayment(userId, paymentId);
    }
}
