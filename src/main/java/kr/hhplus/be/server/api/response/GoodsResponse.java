package kr.hhplus.be.server.api.response;

public class GoodsResponse {
    private Long goodsId;
    private String goodsName;
    private Long price;
    private Long quantity;

    public GoodsResponse(){}

    public GoodsResponse(Long goodsId, String goodsName, Long price, Long quantity) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getGoodsId() {
        return goodsId;
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
