package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/create")
    public ResponseEntity<Object> createPayment(@RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{paymentId}")
    public ResponseEntity<Object> completePayment(@PathVariable("paymentId") Long paymentId, Long userId) {
        PaymentResponse response = paymentService.completePayment(userId, paymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{paymentId}/cancel")
    public ResponseEntity<Object> cancelPayment(@PathVariable("paymentId") Long paymentId, Long userId) {
        PaymentResponse response = paymentService.cancelPayment(userId, paymentId);
        return ResponseEntity.ok(response);
    }
}
