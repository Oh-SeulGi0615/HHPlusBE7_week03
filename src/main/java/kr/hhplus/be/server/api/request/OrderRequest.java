package kr.hhplus.be.server.api.request;

public class OrderRequest {
    private Long goodsId;
    private Long quantity;

    public OrderRequest() {
    }

    public OrderRequest(Long goodsId, Long quantity) {
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
