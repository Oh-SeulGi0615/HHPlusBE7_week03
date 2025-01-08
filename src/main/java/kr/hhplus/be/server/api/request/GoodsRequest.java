package kr.hhplus.be.server.api.request;

public class GoodsRequest {
    private String goodsName;
    private Long price;
    private Long quantity;

    public GoodsRequest(String goodsName, Long price, Long quantity) {
        this.goodsName = goodsName;
        this.price = price;
        this.quantity = quantity;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public Long getPrice() {
        return price;
    }

    public Long getQuantity() {
        return quantity;
    }
}
