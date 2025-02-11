package kr.hhplus.be.server.domain.goods.dto;

public class SalesHistoryServiceDto {
    private Long salesHistoryId;
    private Long goodsId;
    private Long userId;
    private Long quantity;

    public SalesHistoryServiceDto(Long salesHistoryId, Long goodsId, Long userId, Long quantity) {
        this.salesHistoryId = salesHistoryId;
        this.goodsId = goodsId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getSalesHistoryId() {
        return salesHistoryId;
    }

    public void setSalesHistoryId(Long salesHistoryId) {
        this.salesHistoryId = salesHistoryId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
