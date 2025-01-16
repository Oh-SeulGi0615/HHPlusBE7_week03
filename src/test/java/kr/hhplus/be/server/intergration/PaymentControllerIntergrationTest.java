package kr.hhplus.be.server.intergration;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.PaymentRequest;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.goods.GoodsEntity;
import kr.hhplus.be.server.domain.goods.GoodsStockEntity;
import kr.hhplus.be.server.domain.order.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.OrderEntity;
import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.enums.PaymentStatus;
import kr.hhplus.be.server.enums.UserCouponStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
class PaymentControllerIntergrationTest extends IntergrationTest {
    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 성공 케이스")
    void createPayment1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), savedOrder.getOrderId());

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("totalPrice", equalTo(40000));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 성공 케이스 - 쿠폰 포함")
    void createPayment2() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        CouponEntity couponEntity = new CouponEntity("test", 30L, 10L, LocalDate.now().plusDays(10));
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);
        UserCouponEntity userCouponEntity = new UserCouponEntity(savedUser.getUserId(), savedCoupon.getCouponId());
        UserCouponEntity savedMyCoupon = jpaUserCouponRepository.save(userCouponEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), savedOrder.getOrderId(), savedCoupon.getCouponId());

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("totalPrice", equalTo(28000));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 실패 케이스 - 없는 유저")
    void createPayment3() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(userId, orderId);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 실패 케이스 - 없는 주문")
    void createPayment4() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);
        Long orderId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), orderId);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidOrderException"))
                .body("message", equalTo("주문 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 실패 케이스 - 이미 처리된 주문")
    void createPayment5() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        orderEntity.setStatus(OrderStatus.PAID);
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), savedOrder.getOrderId());

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("AlreadyProcessedOrderException"))
                .body("message", equalTo("이미 처리된 주문입니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 실패 케이스 - 없는 쿠폰")
    void createPayment6() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        Long couponId = 1L;

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), savedOrder.getOrderId(), couponId);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidCouponException"))
                .body("message", equalTo("쿠폰 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/create - 결제 생성 실패 케이스 - 사용할 수 없는 쿠폰")
    void createPayment7() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        CouponEntity couponEntity = new CouponEntity("test", 30L, 10L, LocalDate.now().plusDays(10));
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);
        UserCouponEntity userCouponEntity = new UserCouponEntity(savedUser.getUserId(), savedCoupon.getCouponId());
        userCouponEntity.setStatus(UserCouponStatus.USED);
        jpaUserCouponRepository.save(userCouponEntity);

        PaymentRequest paymentRequest = new PaymentRequest(savedUser.getUserId(), savedOrder.getOrderId(), savedCoupon.getCouponId());

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(paymentRequest)
                .when()
                .post("/api/payments/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("CannotUseCouponException"))
                .body("message", equalTo("사용할 수 없는 쿠폰입니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 성공 케이스")
    void completePayment1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        userEntity.setPoint(500000L);
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("PAID"));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 실패 케이스 - 없는 유저")
    void completePayment2() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", userId)
                .body(paymentId)
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 실패 케이스 - 없는 결제 정보")
    void completePayment3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        userEntity.setPoint(500000L);
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        Long paymentId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", paymentId)
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidPaymentException"))
                .body("message", equalTo("결제 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 실패 케이스 - 이미 처리된 결제")
    void completePayment4() {
        // given
        UserEntity userEntity = new UserEntity("test");
        userEntity.setPoint(500000L);
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        paymentEntity.setStatus(PaymentStatus.PAID);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("AlreadyProcessedPaymentException"))
                .body("message", equalTo("이미 처리 완료된 결제입니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 실패 케이스 - 잔액 부족")
    void completePayment5() {
        // given
        UserEntity userEntity = new UserEntity("test");
        userEntity.setPoint(1000L);
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 50L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 30L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("InsufficientPointException"))
                .body("message", equalTo("잔액이 부족합니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId} - 결제 완료 실패 케이스 - 상품 재고 부족")
    void completePayment6() {
        // given
        UserEntity userEntity = new UserEntity("test");
        userEntity.setPoint(500000L);
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 1L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 1L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods1.getGoodsId(), 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), savedGoods2.getGoodsId(), 10L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("GoodsOutOfStockException"))
                .body("message", equalTo("재고가 부족합니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId}/cancel - 결제 취소 성공 케이스")
    void cancelPayment1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("status", equalTo("CANCELED"));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId}/cancel - 결제 취소 실패 케이스 - 없는 유저")
    void cancelPayment2() {
        // given
        Long userId = 1L;
        Long paymentId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", paymentId)
                .body(userId)
                .when()
                .post("/api/payments/{paymentId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId}/cancel - 결제 취소 실패 케이스 - 없는 결제 정보")
    void cancelPayment3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        Long paymentId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", paymentId)
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidPaymentException"))
                .body("message", equalTo("결제 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/payments/{paymentId}/cancel - 결제 취소 실패 케이스 - 이미 처리된 결제")
    void cancelPayment4() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        PaymentEntity paymentEntity = new PaymentEntity(savedOrder.getOrderId(), 40000L);
        paymentEntity.setStatus(PaymentStatus.CANCELED);
        PaymentEntity savedPayment = jpaPaymentRepository.save(paymentEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("paymentId", savedPayment.getPaymentId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/payments/{paymentId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("AlreadyProcessedPaymentException"))
                .body("message", equalTo("이미 처리 완료된 결제입니다."));
    }
}
