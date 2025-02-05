package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.domain.order.dto.MyOrderServiceDto;
import kr.hhplus.be.server.domain.order.dto.OrderServiceDto;
import kr.hhplus.be.server.domain.order.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderFacade {
    private final OrderService orderService;

    public OrderFacade(OrderService orderService) {
        this.orderService = orderService;
    }

    public List<OrderServiceDto> createOrder(Long userId, List<OrderRequest> orderRequestList) {
        return orderService.createOrder(userId, orderRequestList);
    }

    public List<MyOrderServiceDto> getMyAllOrder(Long userId) {
        return orderService.getMyAllOrder(userId);
    }

    public List<OrderServiceDto> getMyDetailOrder(Long userId, Long orderId) {
        return orderService.getMyDetailOrder(userId, orderId);
    }

    public OrderServiceDto cancelOrder(Long userId, Long orderId) {
        return orderService.cancelOrder(userId, orderId);
    }
}
