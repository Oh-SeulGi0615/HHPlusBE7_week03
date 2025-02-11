package kr.hhplus.be.server.domain.goods.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;

@Entity
@Table(name = "sales_history")
public class SalesHistoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long salesHistoryId;

    @Column(nullable = false)
    private Long goodsId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long quantity;

    protected SalesHistoryEntity(){}

    public SalesHistoryEntity(Long goodsId, Long userId, Long quantity) {
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

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }
}
