package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.api.response.PaymentResponse;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.goods.*;
import kr.hhplus.be.server.domain.order.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;
import kr.hhplus.be.server.enums.UserCouponStatus;
import kr.hhplus.be.server.exeption.customExceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final GoodsRepository goodsRepository;
    private final GoodsStockRepository goodsStockRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserCouponRepository userCouponRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository, CouponRepository couponRepository, GoodsRepository goodsRepository, GoodsStockRepository goodsStockRepository, SalesHistoryRepository salesHistoryRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository, UserCouponRepository userCouponRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.goodsRepository = goodsRepository;
        this.goodsStockRepository = goodsStockRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        UserEntity userEntity = userRepository.findByUserId(paymentRequest.getUserId())
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentRequest.getOrderId());
        if (orderEntity.isEmpty()){
            throw new InvalidOrderException("주문 정보를 찾을 수 없습니다.");
        }
        if (orderEntity.get().getStatus() != OrderStatus.WAITING){
            throw new InvalidOrderException("이미 처리된 주문입니다.");
        }

        if (paymentRequest.getCouponId() != 0 && userCouponRepository.findByCouponIdAndUserId(
                paymentRequest.getCouponId(), paymentRequest.getUserId()
                ).isEmpty()){
            throw new InvalidCouponException("쿠폰 정보를 찾을 수 없습니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(paymentRequest.getOrderId());
        Long totalPrice = 0L;
        for (OrderDetailEntity order: orderList) {
            Long price = goodsRepository.findByGoodsId(order.getGoodsId()).get().getPrice();
            Long quantity = order.getQuantity();
            totalPrice += (Long) (price * quantity);
        }

        if (paymentRequest.getCouponId() != null) {
            Long discountRate = couponRepository.findByCouponId(paymentRequest.getCouponId()).get().getDiscountRate();
            totalPrice = (Long) (totalPrice * discountRate / 100);
            PaymentEntity paymentEntity = new PaymentEntity(paymentRequest.getOrderId(), paymentRequest.getCouponId(), totalPrice);
            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

            return new PaymentResponse(
                    savedPayment.getPaymentId(), savedPayment.getOrderId(), savedPayment.getCouponId(), totalPrice
            );
        } else {
            PaymentEntity paymentEntity = new PaymentEntity(paymentRequest.getOrderId(), totalPrice);
            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);
            return new PaymentResponse(
                    savedPayment.getPaymentId(), savedPayment.getOrderId(), totalPrice
            );
        }
    }

    @Transactional
    public PaymentResponse completePayment(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new InvalidPaymentException("이미 처리 완료된 결제입니다.");
        }

        if (userEntity.getPoint() < paymentEntity.getTotalPrice()) {
            throw new InsufficientPointException("잔액이 부족합니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(paymentEntity.getOrderId());
        for (OrderDetailEntity order: orderList) {
            if (goodsStockRepository.findByGoodsId(order.getGoodsId()).get().getQuantity() < order.getQuantity()) {
                throw new GoodsOutOfStockException("재고가 부족합니다.");
            }
            SalesHistoryEntity salesHistoryEntity = new SalesHistoryEntity(order.getGoodsId(), userId, order.getQuantity());
            salesHistoryRepository.save(salesHistoryEntity);

            Optional<GoodsStockEntity> goodsInfo = goodsStockRepository.findByGoodsId(order.getGoodsId());
            goodsInfo.get().setQuantity(goodsInfo.get().getQuantity() - order.getQuantity());
        }

        userEntity.setPoint(userEntity.getPoint() - paymentEntity.getTotalPrice());

        Optional<UserCouponEntity> userCouponEntity = userCouponRepository.findByCouponId(paymentEntity.getCouponId());
        userCouponEntity.get().setStatus(UserCouponStatus.USED);

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.PAID);

        paymentEntity.setStatus(PaymentStatus.PAID);
        return new PaymentResponse(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }

    @Transactional
    public PaymentResponse cancelPayment(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new InvalidPaymentException("이미 처리 완료된 결제입니다.");
        }

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.CANCELED);

        paymentEntity.setStatus(PaymentStatus.CANCELED);
        return new PaymentResponse(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }
}
