package kr.hhplus.be.server.api.response;

public class SalesHistoryResponse {
    private Long goodsId;
    private Long userId;
    private Long quantity;

    public SalesHistoryResponse(Long goodsId, Long userId, Long quantity) {
        this.goodsId = goodsId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
