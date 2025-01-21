package kr.hhplus.be.server.domain.goods;

public class GoodsDomainDto {
    private Long goodsId;
    private String goodsName;
    private Long price;
    private Long quantity;

    public GoodsDomainDto(Long goodsId, String goodsName, Long price, Long quantity) {
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
