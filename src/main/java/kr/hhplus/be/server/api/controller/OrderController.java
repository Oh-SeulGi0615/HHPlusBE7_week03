package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.OrderCreateRequest;
import kr.hhplus.be.server.api.response.MyOrderResponse;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.order.dto.MyOrderServiceDto;
import kr.hhplus.be.server.domain.order.dto.OrderServiceDto;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.facade.OrderFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderFacade orderFacade;
    public OrderController(OrderFacade orderFacade) {
        this.orderFacade = orderFacade;
    }

    @PostMapping("/orders/create")
    public ResponseEntity<Object> orderGoods(@RequestBody OrderCreateRequest request) {
        List<OrderServiceDto> response = orderFacade.createOrder(request.getUserId(), request.getOrders());
        List<OrderResponse> orderResponseList = response.stream().map(orderServiceDto -> new OrderResponse(
                orderServiceDto.getOrderId(), orderServiceDto.getUserId(), orderServiceDto.getGoodsId(), orderServiceDto.getQuantity()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponseList);
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<Object> getMyAllOrder(@PathVariable("userId") Long userId) {
        List<MyOrderServiceDto> response = orderFacade.getMyAllOrder(userId);
        List<MyOrderResponse> myOrderResponseList = response.stream().map(myOrderServiceDto -> new MyOrderResponse(
                myOrderServiceDto.getOrderId(), myOrderServiceDto.getUserId(), myOrderServiceDto.getDueDate(), myOrderServiceDto.getStatus()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(myOrderResponseList);
    }

    @GetMapping("/orders/{userId}/{orderId}")
    public ResponseEntity<Object> getMyDetailOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) {
        List<OrderServiceDto> response = orderFacade.getMyDetailOrder(userId, orderId);
        List<OrderResponse> orderResponseList = response.stream().map(orderServiceDto -> new OrderResponse(
                orderServiceDto.getOrderId(), orderServiceDto.getUserId(), orderServiceDto.getGoodsId(), orderServiceDto.getQuantity()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponseList);
    }

    @PostMapping("/orders/{userId}/{orderId}/cancel")
    public ResponseEntity<Object> cancelOrder(@PathVariable("userId") Long userId, @PathVariable("orderId") Long orderId) {
        OrderServiceDto response = orderFacade.cancelOrder(userId, orderId);
        OrderResponse orderResponse = new OrderResponse(
                response.getOrderId(), response.getUserId(), response.getStatus()
        );
        return ResponseEntity.ok(orderResponse);
    }
}
