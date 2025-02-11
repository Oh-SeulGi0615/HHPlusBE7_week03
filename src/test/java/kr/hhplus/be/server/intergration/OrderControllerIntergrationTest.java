package kr.hhplus.be.server.intergration;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.OrderCreateRequest;
import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.goods.entity.GoodsEntity;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.order.entity.OrderDetailEntity;
import kr.hhplus.be.server.domain.order.entity.OrderEntity;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@ActiveProfiles("test")
class OrderControllerIntergrationTest extends IntergrationTest {
    @Test
    @DisplayName("[POST] /api/orders/create - 주문 생성 성공 케이스")
    void createOrder1() {
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

        List<OrderRequest> orderRequestList = new ArrayList<>();
        OrderRequest orderRequest1 = new OrderRequest(savedGoods1.getGoodsId(), 10L);
        orderRequestList.add(orderRequest1);
        OrderRequest orderRequest2 = new OrderRequest(savedGoods2.getGoodsId(), 20L);
        orderRequestList.add(orderRequest2);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(savedUser.getUserId(), orderRequestList);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(orderCreateRequest)
                .when()
                .post("/api/orders/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].goodsId", equalTo(savedGoods1.getGoodsId().intValue()))
                .body("[0].quantity", equalTo(10))
                .body("[0].status", equalTo("WAITING"))
                .body("[1].goodsId", equalTo(savedGoods2.getGoodsId().intValue()))
                .body("[1].quantity", equalTo(20))
                .body("[1].status", equalTo("WAITING"));
    }

    @Test
    @DisplayName("[POST] /api/orders/create - 주문 생성 실패 케이스 - 없는 유저")
    void createOrder2() {
        // given
        List<OrderRequest> orderRequestList = new ArrayList<>();
        OrderRequest orderRequest1 = new OrderRequest(1L, 10L);
        orderRequestList.add(orderRequest1);
        OrderRequest orderRequest2 = new OrderRequest(2L, 20L);
        orderRequestList.add(orderRequest2);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(1L, orderRequestList);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(orderCreateRequest)
                .when()
                .post("/api/orders/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/orders/create - 주문 생성 실패 케이스 - 없는 상품")
    void createOrder3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        List<OrderRequest> orderRequestList = new ArrayList<>();
        OrderRequest orderRequest1 = new OrderRequest(1L, 10L);
        orderRequestList.add(orderRequest1);
        OrderRequest orderRequest2 = new OrderRequest(2L, 20L);
        orderRequestList.add(orderRequest2);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(savedUser.getUserId(), orderRequestList);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(orderCreateRequest)
                .when()
                .post("/api/orders/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidGoodsException"))
                .body("message", equalTo("상품정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/orders/create - 주문 생성 실패 케이스 - 재고 부족")
    void createOrder4() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 1000L);
        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 3000L);
        GoodsEntity savedGoods1 = jpaGoodsRepository.save(goodsEntity1);
        GoodsEntity savedGoods2 = jpaGoodsRepository.save(goodsEntity2);

        GoodsStockEntity goodsStockEntity1 = new GoodsStockEntity(savedGoods1.getGoodsId(), 1L);
        GoodsStockEntity goodsStockEntity2 = new GoodsStockEntity(savedGoods2.getGoodsId(), 1L);
        jpaGoodsStockRepository.save(goodsStockEntity1);
        jpaGoodsStockRepository.save(goodsStockEntity2);

        List<OrderRequest> orderRequestList = new ArrayList<>();
        OrderRequest orderRequest1 = new OrderRequest(savedGoods1.getGoodsId(), 10L);
        orderRequestList.add(orderRequest1);
        OrderRequest orderRequest2 = new OrderRequest(savedGoods2.getGoodsId(), 20L);
        orderRequestList.add(orderRequest2);

        OrderCreateRequest orderCreateRequest = new OrderCreateRequest(savedUser.getUserId(), orderRequestList);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(orderCreateRequest)
                .when()
                .post("/api/orders/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("GoodsOutOfStockException"))
                .body("message", equalTo("상품 재고가 부족합니다."));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId} - 내 모든 주문 조회 성공 케이스")
    void getMyAllOrder1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId())
                .when()
                .get("/api/orders/{userId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].userId", equalTo(savedUser.getUserId().intValue()))
                .body("[0].orderId", equalTo(savedOrder.getOrderId().intValue()))
                .body("[0].status", equalTo("WAITING"));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId} - 내 모든 주문 조회 실패 케이스 - 없는 유저")
    void getMyAllOrder2() {
        // given
        Long userId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("/api/orders/{userId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId} - 내 모든 주문 조회 실패 케이스 - 없는 주문")
    void getMyAllOrder3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId())
                .when()
                .get("/api/orders/{userId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidOrderException"))
                .body("message", equalTo("주문 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId}/{orderId} - 특정 주문 조회 성공 케이스")
    void getMyDetailOrder1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        OrderDetailEntity orderDetailEntity1 = new OrderDetailEntity(savedOrder.getOrderId(), 1L, 10L);
        OrderDetailEntity orderDetailEntity2 = new OrderDetailEntity(savedOrder.getOrderId(), 2L, 20L);
        jpaOrderDetailRepository.save(orderDetailEntity1);
        jpaOrderDetailRepository.save(orderDetailEntity2);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId())
                .pathParam("orderId", savedOrder.getOrderId())
                .when()
                .get("/api/orders/{userId}/{orderId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].goodsId", equalTo(1))
                .body("[0].quantity", equalTo(10))
                .body("[1].goodsId", equalTo(2))
                .body("[1].quantity", equalTo(20));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId}/{orderId} - 특정 주문 조회 실패 케이스 - 없는 유저")
    void getMyDetailOrder2() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("orderId", orderId)
                .when()
                .get("/api/orders/{userId}/{orderId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[GET] /api/orders/{userId}/{orderId} - 특정 주문 조회 실패 케이스 - 없는 주문")
    void getMyDetailOrder3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);
        Long orderId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId())
                .pathParam("orderId", orderId)
                .when()
                .get("/api/orders/{userId}/{orderId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidOrderException"))
                .body("message", equalTo("주문 정보를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/orders/{userId}/{orderId}/cancel - 주문 취소 성공 케이스")
    void cancelOrder1() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);

        OrderEntity orderEntity = new OrderEntity(savedUser.getUserId());
        OrderEntity savedOrder = jpaOrderRepository.save(orderEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId().intValue())
                .pathParam("orderId",savedOrder.getOrderId().intValue())
                .when()
                .post("/api/orders/{userId}/{orderId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("orderId", equalTo(savedOrder.getOrderId().intValue()))
                .body("userId", equalTo(savedUser.getUserId().intValue()))
                .body("status", equalTo("CANCELED"));
    }

    @Test
    @DisplayName("[POST] /api/orders/{userId}/{orderId}/cancel - 주문 취소 실패 케이스 - 없는 유저")
    void cancelOrder2() {
        // given
        Long userId = 1L;
        Long orderId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .pathParam("orderId", orderId)
                .when()
                .post("/api/orders/{userId}/{orderId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/orders/{userId}/{orderId}/cancel - 주문 취소 실패 케이스 - 없는 주문")
    void cancelOrder3() {
        // given
        UserEntity userEntity = new UserEntity("test");
        UserEntity savedUser = jpaUserRepository.save(userEntity);
        Long orderId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", savedUser.getUserId())
                .pathParam("orderId", orderId)
                .when()
                .post("/api/orders/{userId}/{orderId}/cancel")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidOrderException"))
                .body("message", equalTo("주문 정보를 찾을 수 없습니다."));
    }
}
