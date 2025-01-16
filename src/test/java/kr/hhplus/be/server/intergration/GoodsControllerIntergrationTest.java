package kr.hhplus.be.server.intergration;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ActiveProfiles("test")
class GoodsControllerIntergrationTest extends IntegrationTest {
    @Test
    @DisplayName("[POST] /api/goods/create - 상품 생성 성공 케이스")
    void createGoods1() {
        // given
        GoodsRequest goodsRequest = new GoodsRequest(
                "testGoods1", 3000L, 100L
        );

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(goodsRequest)
                .when()
                .post("/api/goods/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("goodsId", equalTo(1))
                .body("goodsName", equalTo("testGoods1"))
                .body("price", equalTo(3000))
                .body("quantity", equalTo(100));
    }

    @Test
    @DisplayName("[POST] /api/goods - 전체 상품 조회 성공 케이스")
    void findAllGoods() {
        // given
        GoodsRequest goodsRequest = new GoodsRequest(
                "testGoods1", 3000L, 100L
        );

        // when & then
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/goods/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("goodsId", equalTo(1))
                .body("goodsName", equalTo("testGoods1"))
                .body("price", equalTo(3000))
                .body("quantity", equalTo(100));
    }
}
