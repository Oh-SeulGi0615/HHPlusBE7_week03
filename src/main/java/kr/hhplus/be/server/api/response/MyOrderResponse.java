package kr.hhplus.be.server.api.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.OrderStatus;

import java.time.LocalDate;

public class MyOrderResponse {
    private Long orderId;
    private Long userId;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public MyOrderResponse(Long orderId, Long userId, LocalDate dueDate, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
