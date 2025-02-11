package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentServiceDto;
import kr.hhplus.be.server.facade.PaymentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentFacade paymentFacade;

    @Autowired
    public PaymentController(PaymentFacade paymentFacade) {
        this.paymentFacade = paymentFacade;
    }

    @PostMapping("/payments/create")
    public ResponseEntity<Object> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentServiceDto response = paymentFacade.createPayment(
                paymentRequest.getUserId(), paymentRequest.getOrderId(), paymentRequest.getCouponId()
        );
        PaymentResponse paymentResponse = new PaymentResponse(
                response.getPaymentId(),
                response.getOrderId(),
                response.getCouponId(),
                response.getTotalPrice(),
                response.getStatus()
        );
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/payments/{paymentId}")
    public ResponseEntity<Object> confirmPayment(@PathVariable("paymentId") Long paymentId, @RequestBody Long userId) {
        PaymentServiceDto response = paymentFacade.confirmPayment(userId, paymentId);
        PaymentResponse paymentResponse = new PaymentResponse(
                response.getPaymentId(),
                response.getOrderId(),
                response.getCouponId(),
                response.getTotalPrice(),
                response.getStatus()
        );
        return ResponseEntity.ok(paymentResponse);
    }

    @PostMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<Object> cancelPayment(@PathVariable("paymentId") Long paymentId, @RequestBody Long userId) {
        PaymentServiceDto response = paymentFacade.cancelPayment(userId, paymentId);
        PaymentResponse paymentResponse = new PaymentResponse(
                response.getPaymentId(),
                response.getOrderId(),
                response.getCouponId(),
                response.getTotalPrice(),
                response.getStatus()
        );
        return ResponseEntity.ok(paymentResponse);
    }
}
