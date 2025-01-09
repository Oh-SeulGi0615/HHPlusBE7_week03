package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.exeption.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

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
        try {
            PaymentResponse response = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidOrderException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidCouponException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/payments/{id}")
    public ResponseEntity<Object> completePayment(@PathVariable("id") Long paymentId, Long userId) {
        try {
            PaymentResponse response = paymentService.completePayment(userId, paymentId);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPaymentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InsufficientPointException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (GoodsOutOfStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/payments/{id}/cancel")
    public ResponseEntity<Object> cancelPayment(@PathVariable("id") Long paymentId, Long userId) {
        try {
            PaymentResponse response = paymentService.cancelPayment(userId, paymentId);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPaymentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
