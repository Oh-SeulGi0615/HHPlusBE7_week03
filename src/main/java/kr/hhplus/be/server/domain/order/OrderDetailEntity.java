package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;

@Entity
@Table(name = "order_detail")
public class OrderDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long goodsId;

    @Column(nullable = false)
    private Long quantity;

    public OrderDetailEntity(){}

    public OrderDetailEntity(Long orderId, Long goodsId, Long quantity) {
        this.orderId = orderId;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public Long getQuantity() {
        return quantity;
    }
}
