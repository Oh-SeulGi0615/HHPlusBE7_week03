package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.OrderCreateRequest;
import kr.hhplus.be.server.api.response.MyOrderResponse;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.MyOrderDomainDto;
import kr.hhplus.be.server.domain.order.OrderDomainDto;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders/create")
    public ResponseEntity<Object> orderGoods(@RequestBody OrderCreateRequest request) {
        List<OrderDomainDto> response = orderService.createOrder(request.getUserId(), request.getOrders());
        List<OrderResponse> orderResponseList = response.stream().map(orderDomainDto -> new OrderResponse(
                orderDomainDto.getOrderId(), orderDomainDto.getUserId(), orderDomainDto.getGoodsId(), orderDomainDto.getQuantity()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponseList);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<Object> getMyAllOrder(@PathVariable("userId") Long userId) {
        List<MyOrderDomainDto> response = orderService.getMyAllOrder(userId);
        List<MyOrderResponse> myOrderResponseList = response.stream().map(myOrderDomainDto -> new MyOrderResponse(
                myOrderDomainDto.getOrderId(), myOrderDomainDto.getUserId(), myOrderDomainDto.getDueDate(), myOrderDomainDto.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(myOrderResponseList);
    }

    @GetMapping("/orders/{userId}/{orderId}")
    public ResponseEntity<Object> getMyDetailOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) {
        List<OrderDomainDto> response = orderService.getMyDetailOrder(userId, orderId);
        List<OrderResponse> orderResponseList = response.stream().map(orderDomainDto -> new OrderResponse(
                orderDomainDto.getOrderId(), orderDomainDto.getUserId(), orderDomainDto.getGoodsId(), orderDomainDto.getQuantity()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponseList);
    }

    @PostMapping("/orders/{userId}/{orderId}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) {
        OrderDomainDto response = orderService.cancelOrder(userId, orderId);
        OrderResponse orderResponse = new OrderResponse(
                response.getOrderId(), response.getUserId(), response.getStatus()
        );
        return ResponseEntity.ok(orderResponse);
    }
}
