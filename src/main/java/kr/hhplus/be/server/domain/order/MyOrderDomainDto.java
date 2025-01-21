package kr.hhplus.be.server.domain.order;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.OrderStatus;

import java.time.LocalDate;

public class MyOrderDomainDto {
    private Long orderId;
    private Long userId;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public MyOrderDomainDto(Long orderId, Long userId, LocalDate dueDate, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
        this.dueDate = dueDate;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
