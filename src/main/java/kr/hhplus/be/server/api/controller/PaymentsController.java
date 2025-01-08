package kr.hhplus.be.server.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PaymentsController {

    @PostMapping("/payments")
    public ResponseEntity<Object> payments(Long userId, Long orderId, Optional<Long> couponId) {
        List<Long> userList = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Long> couponList = new ArrayList<>(List.of(1L, 2L, 3L));
        Long point = 100000L;

        List<Map<String, Object>> orderList = new ArrayList<>();
        Map<String, Object> order1 = new HashMap<>();
        order1.put("orderId", 1L);
        order1.put("orderDate", LocalDateTime.now());
        order1.put("userId", 1L);
        order1.put("goodsId", 1L);
        order1.put("orderedQuantity", 1L);
        order1.put("price", 100000L);

        Map<String, Object> order2 = new HashMap<>();
        order2.put("orderId", 1L);
        order2.put("orderDate", LocalDateTime.now());
        order2.put("userId", 1L);
        order2.put("goodsId", 2L);
        order2.put("orderedQuantity", 2L);
        order2.put("price", 50000L);

        orderList.add(order1);
        orderList.add(order2);

        if (!userList.contains(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }

        if (couponId.isPresent() && !couponList.contains(couponId.get())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 쿠폰입니다.");
        }

        List<Map<String, Object>> myOrders = new ArrayList<>();
        for (Map<String, Object> order : orderList) {
            if (order.get("orderId").equals(orderId)) {
                 myOrders.add(order);
            }
        }

        if (myOrders == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("주문을 찾을 수 없습니다.");
        }

        Long totalPrice = 0L;
        for (Map<String, Object> order : myOrders) {
            totalPrice += (Long) order.get("price");
        }
        if (totalPrice > point) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잔액이 부족합니다.");
        }

        Map<String, Object> payments = new HashMap<>();
        payments.put("paymentId", 1L);
        payments.put("paymentDate", LocalDateTime.now());
        payments.put("userId", userId);
        payments.put("orderId", orderId);
        payments.put("totalPrice", totalPrice);

        return ResponseEntity.ok(payments);
    }
}
