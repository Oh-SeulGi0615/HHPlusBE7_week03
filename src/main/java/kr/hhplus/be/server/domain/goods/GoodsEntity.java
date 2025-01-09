package kr.hhplus.be.server.domain.goods;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;

@Entity
@Table(name = "goods")
public class GoodsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goodsId;

    @Column(nullable = false)
    private String goodsName;

    @Column(nullable = false)
    private Long price;

    public GoodsEntity(String goodsName, Long price) {
        this.goodsName = goodsName;
        this.price = price;
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

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
