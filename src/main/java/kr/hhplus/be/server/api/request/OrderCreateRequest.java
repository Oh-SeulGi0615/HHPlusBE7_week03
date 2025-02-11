package kr.hhplus.be.server.api.request;

import java.util.List;

public class OrderCreateRequest {
    private Long userId;
    private List<OrderRequest> orders; // goodsId, quantity

    public OrderCreateRequest() {
    }

    public OrderCreateRequest(Long userId, List<OrderRequest> orders) {
        this.userId = userId;
        this.orders = orders;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderRequest> getOrders() {
        return orders;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOrders(List<OrderRequest> orders) {
        this.orders = orders;
    }
}
