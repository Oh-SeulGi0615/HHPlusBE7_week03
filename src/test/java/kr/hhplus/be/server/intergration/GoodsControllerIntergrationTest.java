package kr.hhplus.be.server.intergration;

import io.restassured.http.ContentType;
import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.config.IntergrationTest;
import kr.hhplus.be.server.domain.goods.entity.GoodsEntity;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@ActiveProfiles("test")
class GoodsControllerIntergrationTest extends IntergrationTest {
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
                .body("goodsName", equalTo("testGoods1"))
                .body("price", equalTo(3000))
                .body("quantity", equalTo(100));
    }

    @Test
    @DisplayName("[POST] /api/goods/create - 상품 생성 실패 케이스 - 중복 상품")
    void createGoods2() {
        // given
        GoodsRequest goodsRequest = new GoodsRequest(
                "testGoods1", 3000L, 100L
        );

        GoodsEntity goodsEntity = new GoodsEntity("testGoods1", 5000L);
        jpaGoodsRepository.save(goodsEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .body(goodsRequest)
                .when()
                .post("/api/goods/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("error", equalTo("ExistGoodsException"))
                .body("message", equalTo("이미 등록된 상품입니다."));
    }

    @Test
    @DisplayName("[GET] /api/goods - 전체 상품 조회 성공 케이스")
    void getAllGoods() {
        // given
        GoodsEntity goodsEntity1 = new GoodsEntity("testGoods1", 5000L);
        jpaGoodsRepository.save(goodsEntity1);

        GoodsEntity goodsEntity2 = new GoodsEntity("testGoods2", 15000L);
        jpaGoodsRepository.save(goodsEntity2);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/goods")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", greaterThanOrEqualTo(2))
                .body("[0].goodsName", equalTo("testGoods1"))
                .body("[0].price", equalTo(5000))
                .body("[1].goodsName", equalTo("testGoods2"))
                .body("[1].price", equalTo(15000));
    }

    @Test
    @DisplayName("[GET] /api/goods/{goodsId} - 특정 상품 조회 성공 케이스")
    void getOneGoodsInfo1() {
        // given
        GoodsEntity goodsEntity = new GoodsEntity("testGoods1", 5000L);
        GoodsEntity savedGoods = jpaGoodsRepository.save(goodsEntity);
        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(savedGoods.getGoodsId(), 100L);
        jpaGoodsStockRepository.save(goodsStockEntity);

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("goodsId", savedGoods.getGoodsId())
                .when()
                .get("/api/goods/{goodsId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("goodsName", equalTo("testGoods1"))
                .body("price", equalTo(5000))
                .body("quantity", equalTo(100));
    }

    @Test
    @DisplayName("[GET] /api/goods/{goodsId} - 특정 상품 조회 실패 케이스 - 없는 상품")
    void getOneGoodsInfo2() {
        // given
        Long goodsId = 999L;

        // when & then
        given()
                .contentType(ContentType.JSON)
                .pathParam("goodsId", goodsId)
                .when()
                .get("/api/goods/{goodsId}")
                .then()
                .log().all()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("InvalidGoodsException"))
                .body("message", equalTo("상품 정보를 찾을 수 없습니다."));
    }
}
