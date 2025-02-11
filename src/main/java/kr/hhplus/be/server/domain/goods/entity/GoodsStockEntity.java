package kr.hhplus.be.server.domain.goods.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;

@Entity
@Table(name = "goods_stock")
public class GoodsStockEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long goodsStockId;

    @Column(nullable = false)
    private Long goodsId;

    @Column(nullable = false)
    private Long quantity;

    protected GoodsStockEntity(){}

    public GoodsStockEntity(Long goodsId, Long quantity) {
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public Long getGoodsStockId() {
        return goodsStockId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
