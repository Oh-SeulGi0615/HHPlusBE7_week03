package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.goods.*;
import kr.hhplus.be.server.domain.order.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.payment.PaymentRepository;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private GoodsStockRepository goodsStockRepository;

    @Mock
    private SalesHistoryRepository salesHistoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제생성_쿠폰없음_성공케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long goodsId = 1L;
        String goodsName = "test goods";
        Long price = 1000L;
        Long quantity = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId);

        UserEntity userEntity = new UserEntity(userName);
        OrderEntity orderEntity = new OrderEntity(userId);
        GoodsEntity goodsEntity = new GoodsEntity(goodsName, price);
        OrderDetailEntity orderDetail = new OrderDetailEntity(orderId, goodsId, quantity);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, price);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderDetailRepository.findAllByOrderId(orderId)).thenReturn(List.of(orderDetail));
        when(goodsRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goodsEntity));
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);

        // when
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // then
        assertNotNull(response);
        assertEquals(orderId, response.getOrderId());
        assertEquals(price, response.getTotalPrice());
    }

    @Test
    void 결제생성_쿠폰있음_성공케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long goodsId = 1L;
        String goodsName = "test goods";
        Long goodsPrice = 1000L;
        Long quantity = 1L;

        Long couponId = 1L;
        Long discountRate = 10L;
        Long afterDiscountPrice = 900L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId, couponId);

        UserEntity userEntity = new UserEntity(userName);
        OrderEntity orderEntity = new OrderEntity(userId);
        GoodsEntity goodsEntity = new GoodsEntity(goodsName, goodsPrice);
        OrderDetailEntity orderDetail = new OrderDetailEntity(orderId, goodsId, quantity);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, goodsPrice);
        CouponEntity couponEntity = new CouponEntity("test", discountRate, 10L, LocalDate.now().plusDays(10));
        UserCouponEntity userCouponEntity = new UserCouponEntity(userId, couponId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderDetailRepository.findAllByOrderId(orderId)).thenReturn(List.of(orderDetail));
        when(goodsRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goodsEntity));
        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(paymentEntity);
        when(couponRepository.findByCouponId(couponId)).thenReturn(Optional.of(couponEntity));
        when(userCouponRepository.findByCouponIdAndUserId(couponId, userId)).thenReturn(Optional.of(userCouponEntity));

        // when
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // then
        assertNotNull(response);
        assertEquals(orderId, response.getOrderId());
        assertEquals(afterDiscountPrice, response.getTotalPrice());
    }

    @Test
    void 결제생성_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId);
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidUserException.class, () -> paymentService.createPayment(paymentRequest));
    }

    @Test
    void 결제생성_없는주문_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId);
        UserEntity userEntity = new UserEntity(userName);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));

        // when & then
        assertThrows(InvalidOrderException.class, () -> paymentService.createPayment(paymentRequest));
    }

    @Test
    void 결제생성_없는쿠폰_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long couponId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId, couponId);
        UserEntity userEntity = new UserEntity(userName);
        OrderEntity orderEntity = new OrderEntity(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(orderEntity));
        when(userCouponRepository.findByCouponIdAndUserId(couponId, userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidCouponException.class, () -> paymentService.createPayment(paymentRequest));
    }

    @Test
    void 결제완료_성공케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long goodsId = 1L;
        Long totalPrice = 1000L;
        Long quantity = 1L;
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(3000L);
        userEntity.setUserId(userId);

        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, quantity);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setPaymentId(paymentId);
        paymentEntity.setStatus(PaymentStatus.WAITING);

        OrderEntity orderEntity = new OrderEntity(userId);
        orderEntity.setStatus(OrderStatus.PAID);

        PaymentEntity paidEntity = new PaymentEntity(orderId, totalPrice);
        paidEntity.setPaymentId(paymentId);
        paidEntity.setStatus(PaymentStatus.PAID);

        OrderDetailEntity orderDetailEntity = new OrderDetailEntity(orderId, goodsId, quantity);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));
        when(orderDetailRepository.findAllByOrderId(orderId)).thenReturn(List.of(orderDetailEntity));
        when(goodsStockRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goodsStockEntity));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(orderEntity));

        // when
        PaymentResponse response = paymentService.completePayment(userId, paymentId);

        // then
        assertNotNull(response);
        assertEquals(PaymentStatus.PAID, response.getStatus());
    }

    @Test
    void 결제완료_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(3000L);
        userEntity.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidUserException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제완료_없는결제정보_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(3000L);
        userEntity.setUserId(userId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidPaymentException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제완료_이미처리된결제_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long totalPrice = 1000L;
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(3000L);
        userEntity.setUserId(userId);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setPaymentId(paymentId);
        paymentEntity.setStatus(PaymentStatus.PAID);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));

        // when & then
        assertThrows(AlreadyProcessedPaymentException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제완료_잔액부족_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long totalPrice = 1000L;
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(500L);
        userEntity.setUserId(userId);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setPaymentId(paymentId);
        paymentEntity.setStatus(PaymentStatus.WAITING);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));

        // when & then
        assertThrows(InsufficientPointException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제완료_재고부족_실패케이스() {
        // given
        Long userId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long goodsId = 1L;
        Long totalPrice = 1000L;
        Long stockQuantity = 1L;
        Long needQuantity = 2L;
        Long paymentId = 1L;

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setPoint(3000L);
        userEntity.setUserId(userId);
        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, stockQuantity);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setPaymentId(paymentId);
        paymentEntity.setStatus(PaymentStatus.WAITING);

        PaymentEntity paidEntity = new PaymentEntity(orderId, totalPrice);
        paidEntity.setPaymentId(paymentId);
        paidEntity.setStatus(PaymentStatus.PAID);

        OrderDetailEntity orderDetailEntity = new OrderDetailEntity(orderId, goodsId, needQuantity);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));
        when(orderDetailRepository.findAllByOrderId(orderId)).thenReturn(List.of(orderDetailEntity));
        when(goodsStockRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goodsStockEntity));

        // when & then
        assertThrows(GoodsOutOfStockException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제취소_성공케이스() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long totalPrice = 1000L;

        UserEntity userEntity = new UserEntity(userName);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setStatus(PaymentStatus.WAITING);

        PaymentEntity canceledPaymentEntity = new PaymentEntity(orderId, totalPrice);
        canceledPaymentEntity.setStatus(PaymentStatus.CANCELED);

        OrderEntity orderEntity = new OrderEntity(userId);
        orderEntity.setStatus(OrderStatus.CANCELED);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(orderEntity));

        // when
        PaymentResponse response = paymentService.cancelPayment(userId, paymentId);

        // then
        assertNotNull(response);
        assertEquals(PaymentStatus.CANCELED, response.getStatus());
    }

    @Test
    void 결제취소_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;

        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidUserException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제취소_없는결제정보_실패케이스() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;
        String userName = "test";
        UserEntity userEntity = new UserEntity(userName);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidPaymentException.class, () -> paymentService.completePayment(userId, paymentId));
    }

    @Test
    void 결제취소_이미처리된결제_실패케이스() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;
        String userName = "test";
        Long orderId = 1L;
        Long totalPrice = 1000L;

        UserEntity userEntity = new UserEntity(userName);
        PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
        paymentEntity.setStatus(PaymentStatus.PAID);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(paymentRepository.findByPaymentId(paymentId)).thenReturn(Optional.of(paymentEntity));

        // when & then
        assertThrows(AlreadyProcessedPaymentException.class, () -> paymentService.completePayment(userId, paymentId));
    }
}