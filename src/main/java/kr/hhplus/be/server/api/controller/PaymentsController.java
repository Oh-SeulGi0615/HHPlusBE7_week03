package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PaymentsController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentsController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payments/create")
    public ResponseEntity<Object> createPayment(PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{id}")
    public ResponseEntity<Object> completePayment(@PathVariable("id") Long paymentId, Long userId) {
        PaymentResponse response = paymentService.completePayment(userId, paymentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payments/{id}/cancel")
    public ResponseEntity<Object> cancelPayment(@PathVariable("id") Long paymentId, Long userId) {
        PaymentResponse response = paymentService.cancelPayment(userId, paymentId);
        return ResponseEntity.ok(response);
    }
}
