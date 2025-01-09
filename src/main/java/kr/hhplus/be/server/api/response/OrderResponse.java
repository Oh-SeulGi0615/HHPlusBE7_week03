package kr.hhplus.be.server.api.response;

import kr.hhplus.be.server.enums.OrderStatus;

public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Enum status;
    private Long goodsId;
    private Long quantity;

    public OrderResponse(Long orderId, Long userId, Long goodsId, Long quantity) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = OrderStatus.WAITING;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public OrderResponse(Long orderId, Long userId, Enum status) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public Enum getStatus() {
        return status;
    }
}
