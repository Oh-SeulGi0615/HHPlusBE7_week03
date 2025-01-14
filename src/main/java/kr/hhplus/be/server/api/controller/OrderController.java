package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.OrderCreateRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> orderGoods(@RequestBody OrderCreateRequest request) {
        List<OrderResponse> response = orderService.createOrder(request.getUserId(), request.getOrders());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<Object> getMyAllOrder(@PathVariable("userId") Long userId) {
        List<OrderEntity> response = orderService.getMyAllOrder(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{userId}/{orderId}")
    public ResponseEntity<Object> getMyDetailOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) {
        List<OrderResponse> response = orderService.getMyDetailOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/orders/{userId}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable("userId") Long userId, Long orderId) {
        OrderResponse response = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(response);
    }
}
