package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.repository.GoodsStockRepository;
import kr.hhplus.be.server.domain.goods.repository.SalesHistoryRepository;
import kr.hhplus.be.server.domain.order.entity.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.entity.OrderEntity;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.entity.PaymentEntity;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.payment.service.PaymentService;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.exeption.customExceptions.InsufficientPointException;
import kr.hhplus.be.server.infra.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentEventTest {
    @Mock
    private UserRepository userRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private GoodsStockRepository goodsStockRepository;
    @Mock private OrderDetailRepository orderDetailRepository;
    @Mock private SalesHistoryRepository salesHistoryRepository;
    @Mock private UserCouponRepository userCouponRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private KafkaProducer kafkaProducer;

    @InjectMocks
    private PaymentService paymentService;

    private UserEntity user;
    private PaymentEntity payment;
    private OrderEntity order;
    private OrderDetailEntity orderDetail;
    private GoodsStockEntity goodsStock;

    @BeforeEach
    void setUp() {
        // 사용자 설정 (잔액 10000)
        user = new UserEntity("test");
        user.setUserId(1L);
        user.setPoint(10000L);

        // 결제 정보 설정 (5000원 결제)
        payment = new PaymentEntity(1L, 5000L);
        payment.setPaymentId(1L);

        // 주문 설정
        order = new OrderEntity(1L);
        order.setOrderId(1L);

        // 주문 상세 정보 설정
        orderDetail = new OrderDetailEntity(1L, 1L, 2L);

        // 재고 설정
        goodsStock = new GoodsStockEntity(1L, 10L);
    }

    @Test
    @DisplayName("confirmPayment 이벤트 발생 성공 테스트")
    void confirmPayment1() {
        // Given: Mock 데이터 설정
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(user));
        when(paymentRepository.findByPaymentId(1L)).thenReturn(Optional.of(payment));
        when(orderRepository.findByOrderId(1L)).thenReturn(Optional.of(order));
        when(orderDetailRepository.findAllByOrderId(1L)).thenReturn(List.of(orderDetail));
        when(goodsStockRepository.findByGoodsId(1L)).thenReturn(Optional.of(goodsStock));

        // When: 결제 확인 요청
        paymentService.confirmPayment(1L, 1L);

        // Then: 이벤트 발생 검증
        verify(kafkaProducer, times(1)).sendMessage(anyString());
    }

    @Test
    @DisplayName("confirmPayment 로직 실패로 인한 이벤트 미발생 테스트")
    void confirmPayment2() {
        // Given: 사용자 잔액 부족
        user.setPoint(1000L);
        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(user));
        when(paymentRepository.findByPaymentId(1L)).thenReturn(Optional.of(payment));

        // When & Then: 예외 발생 및 이벤트 미발생 검증
        assertThrows(InsufficientPointException.class, () -> paymentService.confirmPayment(1L, 1L));
        verify(kafkaProducer, never()).sendMessage(anyString());
    }
}
