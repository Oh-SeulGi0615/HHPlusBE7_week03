package kr.hhplus.be.server.api.response;

public class SalesHistoryResponse {
    private Long salesHistoryId;
    private Long goodsId;
    private Long userId;
    private Long quantity;

    public SalesHistoryResponse(Long salesHistoryId, Long goodsId, Long userId, Long quantity) {
        this.salesHistoryId = salesHistoryId;
        this.goodsId = goodsId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getSalesHistoryId() {
        return salesHistoryId;
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
