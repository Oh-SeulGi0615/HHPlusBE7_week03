package kr.hhplus.be.server.intergration;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.user.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@ActiveProfiles("test")
class CouponControllerIntergrationTest extends IntegrationTest {
    @Test
    @DisplayName("[POST] /api/coupons/create - 쿠폰 생성 성공 케이스")
    void createCoupon1() {
        // given
        CreateCouponRequest createCouponRequest = new CreateCouponRequest("test", 10L, 10L, LocalDate.now().plusDays(10));

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(createCouponRequest)
                .when()
                .post("/api/coupons/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("couponId", equalTo(1))
                .body("couponName", equalTo("test"))
                .body("discountRate", equalTo(10))
                .body("capacity", equalTo(10))
                .body("dueDate", equalTo("2025-01-26"));
    }

    @Test
    @DisplayName("[POST] /api/coupons/create - 쿠폰 생성 실패 케이스 - 중복 쿠폰")
    void createCoupon2() {
        // given
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, 10L, LocalDate.now().plusDays(10)
        );
        jpaCouponRepository.save(couponEntity);

        CreateCouponRequest createCouponRequest = new CreateCouponRequest("testCoupon1", 10L, 10L, LocalDate.now().plusDays(10));

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(createCouponRequest)
                .when()
                .post("/api/coupons/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("ExistCouponException"))
                .body("message", equalTo("이미 등록된 쿠폰입니다."));
    }

    @Test
    @DisplayName("[GET] /api/coupons - 모든 쿠폰 조회 성공 케이스")
    void allCouponList() {
        // given
        CouponEntity couponEntity1 = new CouponEntity(
                "testCoupon1", 10L, 10L, LocalDate.now().plusDays(10)
        );
        CouponEntity couponEntity2 = new CouponEntity(
                "testCoupon2", 20L, 20L, LocalDate.now().plusDays(15)
        );
        jpaCouponRepository.save(couponEntity1);
        jpaCouponRepository.save(couponEntity2);


        // when & then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/coupons")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].couponName", equalTo("testCoupon1"))
                .body("[1].couponName", equalTo("testCoupon2"));
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 성공 케이스")
    void getCoupon1() {
        // given
        UserEntity userEntity = new UserEntity("testUser");
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, 10L, LocalDate.now().plusDays(10)
        );

        UserEntity savedUser = jpaUserRepository.save(userEntity);
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);


        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("couponId", savedCoupon.getCouponId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/coupons/{couponId}/get")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("couponId", equalTo(savedCoupon.getCouponId().intValue()))
                .body("couponName", equalTo("testCoupon1"));
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 실패 케이스 - 없는 쿠폰")
    void getCoupon2() {
        // given
        UserEntity userEntity = new UserEntity("testUser");

        UserEntity savedUser = jpaUserRepository.save(userEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("couponId", 1L)
                .body(savedUser.getUserId())
                .when()
                .post("/api/coupons/{couponId}/get")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidCouponException"))
                .body("message", equalTo("존재하지 않는 쿠폰입니다."));
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 실패 케이스 - 이미 발급받은 쿠폰")
    void getCoupon3() {
        // given
        UserEntity userEntity = new UserEntity("testUser");
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, 10L, LocalDate.now().plusDays(10)
        );

        UserEntity savedUser = jpaUserRepository.save(userEntity);
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        UserCouponEntity userCouponEntity = new UserCouponEntity(savedUser.getUserId(), savedCoupon.getCouponId());
        jpaUserCouponRepository.save(userCouponEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("couponId", savedCoupon.getCouponId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/coupons/{couponId}/get")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("ExistCouponException"))
                .body("message", equalTo("이미 발급받은 쿠폰입니다."));
    }

    @Test
    @DisplayName("[POST] /api/coupons/{couponId}/get - 쿠폰 발급 실패 케이스 - 쿠폰 재고 소진")
    void getCoupon4() {
        // given
        UserEntity userEntity = new UserEntity("testUser");
        CouponEntity couponEntity = new CouponEntity(
                "testCoupon1", 10L, 0L, LocalDate.now().plusDays(10)
        );

        UserEntity savedUser = jpaUserRepository.save(userEntity);
        CouponEntity savedCoupon = jpaCouponRepository.save(couponEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("couponId", savedCoupon.getCouponId())
                .body(savedUser.getUserId())
                .when()
                .post("/api/coupons/{couponId}/get")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("CouponOutOfStockException"))
                .body("message", equalTo("쿠폰이 모두 소진되었습니다."));
    }
}
