package kr.hhplus.be.server.domain.order;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import kr.hhplus.be.server.enums.OrderStatus;

public class OrderServiceDto {
    private Long orderId;
    private Long userId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long goodsId;
    private Long quantity;

    public OrderServiceDto(Long orderId, Long userId, Long goodsId, Long quantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = OrderStatus.WAITING;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public OrderServiceDto(Long orderId, Long userId, OrderStatus status) {
        this.orderId = orderId;
        this.userId = userId;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
