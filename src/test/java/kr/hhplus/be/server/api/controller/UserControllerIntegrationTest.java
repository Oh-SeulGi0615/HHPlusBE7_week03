package kr.hhplus.be.server.api.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.UserRequest;
import kr.hhplus.be.server.domain.coupon.CouponEntity;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.coupon.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.UserCouponRepository;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        UserEntity user1 = new UserEntity("test1");
        user1.setPoint(9500000L);
        userRepository.save(user1);

        UserEntity user2 = new UserEntity("test2");
        user2.setPoint(5000L);
        userRepository.save(user2);

        CouponEntity coupon1 = new CouponEntity("coupon1", 10L, 10L, LocalDate.now().plusDays(10));
        couponRepository.save(coupon1);

        UserCouponEntity myCoupon1 = new UserCouponEntity(1L, 1L);
        userCouponRepository.save(myCoupon1);
    }

    @Test
    @DisplayName("[POST] /api/user/create - 유저 생성 성공 케이스")
    void createUser1() {
        // given
        UserRequest userRequest = new UserRequest("test3");

        // when & then
        given()
            .contentType(ContentType.JSON)
            .body(userRequest)
        .when()
            .post("/api/users/create")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .body("userId", equalTo(3))
            .body("userName", equalTo("test3"))
            .body("point", equalTo(0));
    }

    @Test
    @DisplayName("[POST] /api/users/create - 유저 생성 실패 케이스 - 중복 유저")
    void createUser2() {
        // given
        UserRequest userRequest = new UserRequest("test1");

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .when()
                .post("/api/users/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("ExistUserException"))
                .body("message", equalTo("이미 등록된 유저입니다."));
    }

    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 포인트 충전 성공 케이스")
    void chargePoint1() {
        // given
        Long userId = 1L;
        Long point = 5000L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(point)
                .when()
                .post("/api/users/{userId}/points/charge")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(userId.intValue()))
                .body("point", equalTo(9505000));
    }

    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 포인트 충전 실패 케이스 - 없는 유저")
    void chargePoint2() {
        // given
        Long userId = 5L;
        Long point = 5000L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(point)
                .when()
                .post("/api/users/{userId}/points/charge")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 포인트 충전 실패 케이스 - 마이너스 금액 충전 요청")
    void chargePoint3() {
        // given
        Long userId = 1L;
        Long point = -5000L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(point)
                .when()
                .post("/api/users/{userId}/points/charge")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("InvalidPointException"))
                .body("message", equalTo("충전 가능한 금액은 0원 초과 10원 단위입니다."));
    }

    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 포인트 충전 실패 케이스 - 1회 충전 요청 가능 금액 초과")
    void chargePoint4() {
        // given
        Long userId = 1L;
        Long point = 1500000L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(point)
                .when()
                .post("/api/users/{userId}/points/charge")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("InvalidPointException"))
                .body("message", equalTo("1회 충전 가능한 금액은 최대 1,000,000원 입니다."));
    }

    @Test
    @DisplayName("[POST] /api/users/{userId}/points/charge - 포인트 충전 실패 케이스 - 총 보유 포인트 한도 초과")
    void chargePoint5() {
        // given
        Long userId = 1L;
        Long point = 800000L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .body(point)
                .when()
                .post("/api/users/{userId}/points/charge")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("InvalidPointException"))
                .body("message", equalTo("보유할 수 있는 최대 금액은 10,000,000원 입니다."));
    }

    @Test
    @DisplayName("[GET] /api/users/{userId}/points/check - 포인트 조회 성공 케이스")
    void checkPoint1() {
        // given
        Long userId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("/api/users/{userId}/points/check")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(userId.intValue()))
                .body("point", equalTo(9500000));
    }

    @Test
    @DisplayName("[GET] /api/users/{userId}/points/check - 포인트 조회 실패 케이스 - 없는 유저")
    void checkPoint2() {
        // given
        Long userId = 5L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("/api/users/{userId}/points/check")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("[GET] /api/users/{userId}/coupons - 쿠폰 조회 성공 케이스")
    void checkMyCoupon1() {
        // given
        Long userId = 1L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("/api/users/{userId}/coupons")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].userId", equalTo(userId.intValue()))
                .body("[0].couponId", equalTo(1));
    }

    @Test
    @DisplayName("[GET] /api/users/{userId}/coupons - 쿠폰 조회 실패 케이스 - 없는 유저")
    void checkMyCoupon2() {
        // given
        Long userId = 5L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get("/api/users/{userId}/coupons")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidUserException"))
                .body("message", equalTo("유저를 찾을 수 없습니다."));
    }
}