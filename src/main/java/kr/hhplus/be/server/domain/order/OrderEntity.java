package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.enums.OrderStatus;

import java.time.LocalDate;

@Entity
@Table(name = "order")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Enum status;

    public OrderEntity(Long userId) {
        this.userId = userId;
        this.dueDate = LocalDate.now().plusDays(3);
        this.status = OrderStatus.WAITING;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public Enum getStatus() {
        return status;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(Enum status) {
        this.status = status;
    }
}
