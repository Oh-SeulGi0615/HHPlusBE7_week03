package kr.hhplus.be.server.domain.payment.service;

import kr.hhplus.be.server.aop.DistributedLock;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.entity.SalesHistoryEntity;
import kr.hhplus.be.server.domain.goods.repository.GoodsRepository;
import kr.hhplus.be.server.domain.goods.repository.GoodsStockRepository;
import kr.hhplus.be.server.domain.goods.repository.SalesHistoryRepository;
import kr.hhplus.be.server.domain.order.entity.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.repository.OrderDetailRepository;
import kr.hhplus.be.server.domain.order.entity.OrderEntity;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.payment.dto.PaymentServiceDto;
import kr.hhplus.be.server.domain.payment.entity.PaymentEntity;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
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

    public PaymentServiceDto createPayment(Long userId, Long orderId, Long couponId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(orderId);
        if (orderEntity.isEmpty()){
            throw new InvalidOrderException("주문 정보를 찾을 수 없습니다.");
        }
        if (orderEntity.get().getStatus() != OrderStatus.WAITING){
            throw new AlreadyProcessedOrderException("이미 처리된 주문입니다.");
        }

        if (couponId != 0L  && userCouponRepository.findByCouponIdAndUserId(
                couponId, userId
                ).isEmpty()){
            throw new InvalidCouponException("쿠폰 정보를 찾을 수 없습니다.");
        }
        if (couponId != 0L && userCouponRepository.findByCouponIdAndUserId(
                couponId, userId
        ).get().isStatus() != UserCouponStatus.AVAILABLE){
            throw new CannotUseCouponException("사용할 수 없는 쿠폰입니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(orderId);
        Long totalPrice = 0L;
        for (OrderDetailEntity order: orderList) {
            Long price = goodsRepository.findByGoodsId(order.getGoodsId()).get().getPrice();
            Long quantity = order.getQuantity();
            totalPrice += (Long) (price * quantity);
        }

        if (couponId != 0L) {
            Long discountRate = couponRepository.findByCouponId(couponId).get().getDiscountRate();
            totalPrice = (Long) (totalPrice * (100 - discountRate) / 100);
            PaymentEntity paymentEntity = new PaymentEntity(orderId, couponId, totalPrice);
            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

            return new PaymentServiceDto(
                    savedPayment.getPaymentId(), savedPayment.getOrderId(), savedPayment.getCouponId(), totalPrice
            );
        } else {
            PaymentEntity paymentEntity = new PaymentEntity(orderId, totalPrice);
            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);
            return new PaymentServiceDto(
                    savedPayment.getPaymentId(), savedPayment.getOrderId(), totalPrice
            );
        }
    }

    @DistributedLock(key = "CONFIRM_PAYMENT", waitTime = 5, leaseTime = 10)
    public PaymentServiceDto confirmPayment(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new AlreadyProcessedPaymentException("이미 처리 완료된 결제입니다.");
        }

        if (userEntity.getPoint() < paymentEntity.getTotalPrice()) {
            throw new InsufficientPointException("잔액이 부족합니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(paymentEntity.getOrderId());
        for (OrderDetailEntity order: orderList) {
            if (goodsStockRepository.findByGoodsId(order.getGoodsId()).get().getQuantity() < order.getQuantity()) {
                throw new GoodsOutOfStockException("재고가 부족합니다.");
            }
            Optional<GoodsStockEntity> goodsInfo = goodsStockRepository.findByGoodsId(order.getGoodsId());
            goodsInfo.get().setQuantity(goodsInfo.get().getQuantity() - order.getQuantity());
            goodsStockRepository.save(goodsInfo.get());

            SalesHistoryEntity salesHistoryEntity = new SalesHistoryEntity(order.getGoodsId(), userId, order.getQuantity());
            salesHistoryRepository.save(salesHistoryEntity);
        }

        userEntity.setPoint(userEntity.getPoint() - paymentEntity.getTotalPrice());
        userRepository.save(userEntity);

        if (paymentEntity.getCouponId() != null) {
            Optional<UserCouponEntity> userCouponEntity = userCouponRepository.findByCouponId(paymentEntity.getCouponId());
            userCouponEntity.get().setStatus(UserCouponStatus.USED);
        }

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.PAID);
        orderRepository.save(orderEntity.get());
        paymentEntity.setStatus(PaymentStatus.PAID);
        paymentRepository.save(paymentEntity);

        return new PaymentServiceDto(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }

    @Transactional
    public PaymentServiceDto confirmPaymentOptimistic(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new AlreadyProcessedPaymentException("이미 처리 완료된 결제입니다.");
        }

        if (userEntity.getPoint() < paymentEntity.getTotalPrice()) {
            throw new InsufficientPointException("잔액이 부족합니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(paymentEntity.getOrderId());
        for (OrderDetailEntity order: orderList) {
            if (goodsStockRepository.findByGoodsIdOptimistic(order.getGoodsId()).get().getQuantity() < order.getQuantity()) {
                throw new GoodsOutOfStockException("재고가 부족합니다.");
            }
            Optional<GoodsStockEntity> goodsInfo = goodsStockRepository.findByGoodsIdOptimistic(order.getGoodsId());
            goodsInfo.get().setQuantity(goodsInfo.get().getQuantity() - order.getQuantity());

            SalesHistoryEntity salesHistoryEntity = new SalesHistoryEntity(order.getGoodsId(), userId, order.getQuantity());
            salesHistoryRepository.save(salesHistoryEntity);
        }

        userEntity.setPoint(userEntity.getPoint() - paymentEntity.getTotalPrice());

        if (paymentEntity.getCouponId() != null) {
            Optional<UserCouponEntity> userCouponEntity = userCouponRepository.findByCouponId(paymentEntity.getCouponId());
            userCouponEntity.get().setStatus(UserCouponStatus.USED);
        }

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.PAID);

        paymentEntity.setStatus(PaymentStatus.PAID);
        return new PaymentServiceDto(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }

    @Transactional
    public PaymentServiceDto confirmPaymentPessimistic(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new AlreadyProcessedPaymentException("이미 처리 완료된 결제입니다.");
        }

        if (userEntity.getPoint() < paymentEntity.getTotalPrice()) {
            throw new InsufficientPointException("잔액이 부족합니다.");
        }

        List<OrderDetailEntity> orderList = orderDetailRepository.findAllByOrderId(paymentEntity.getOrderId());
        for (OrderDetailEntity order: orderList) {
            if (goodsStockRepository.findByGoodsIdPessimistic(order.getGoodsId()).get().getQuantity() < order.getQuantity()) {
                throw new GoodsOutOfStockException("재고가 부족합니다.");
            }
            Optional<GoodsStockEntity> goodsInfo = goodsStockRepository.findByGoodsIdPessimistic(order.getGoodsId());
            goodsInfo.get().setQuantity(goodsInfo.get().getQuantity() - order.getQuantity());

            SalesHistoryEntity salesHistoryEntity = new SalesHistoryEntity(order.getGoodsId(), userId, order.getQuantity());
            salesHistoryRepository.save(salesHistoryEntity);
        }

        userEntity.setPoint(userEntity.getPoint() - paymentEntity.getTotalPrice());

        if (paymentEntity.getCouponId() != null) {
            Optional<UserCouponEntity> userCouponEntity = userCouponRepository.findByCouponId(paymentEntity.getCouponId());
            userCouponEntity.get().setStatus(UserCouponStatus.USED);
        }

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.PAID);

        paymentEntity.setStatus(PaymentStatus.PAID);
        return new PaymentServiceDto(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }

    @Transactional
    public PaymentServiceDto cancelPayment(Long userId, Long paymentId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        PaymentEntity paymentEntity = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new InvalidPaymentException("결제 정보를 찾을 수 없습니다."));

        if (paymentEntity.getStatus() != PaymentStatus.WAITING) {
            throw new AlreadyProcessedPaymentException("이미 처리 완료된 결제입니다.");
        }

        Optional<OrderEntity> orderEntity = orderRepository.findByOrderId(paymentEntity.getOrderId());
        orderEntity.get().setStatus(OrderStatus.CANCELED);

        paymentEntity.setStatus(PaymentStatus.CANCELED);
        return new PaymentServiceDto(
                paymentEntity.getPaymentId(),
                paymentEntity.getOrderId(),
                paymentEntity.getCouponId(),
                paymentEntity.getTotalPrice(),
                paymentEntity.getStatus()
        );
    }
}
