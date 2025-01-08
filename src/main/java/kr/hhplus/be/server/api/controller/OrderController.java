package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.exeption.GoodsOutOfStockException;
import kr.hhplus.be.server.exeption.InvalidGoodsException;
import kr.hhplus.be.server.exeption.InvalidUserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> orderGoods(Long userId, List<OrderRequest> orderRequestList) {
        try {
            List<OrderResponse> response = orderService.createOrder(userId, orderRequestList);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidGoodsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (GoodsOutOfStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Object> getMyOrder(Long userId) {
        try {
            List<OrderResponse> response = orderService.getMyOrder(userId);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
