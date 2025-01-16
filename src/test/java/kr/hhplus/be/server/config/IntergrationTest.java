package kr.hhplus.be.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.goods.GoodsService;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.infra.coupon.JpaCouponRepository;
import kr.hhplus.be.server.infra.goods.JpaGoodsRepository;
import kr.hhplus.be.server.infra.goods.JpaGoodsStockRepository;
import kr.hhplus.be.server.infra.goods.JpaSalesHistoryRepository;
import kr.hhplus.be.server.infra.order.JpaOrderDetailRepository;
import kr.hhplus.be.server.infra.order.JpaOrderRepository;
import kr.hhplus.be.server.infra.payment.JpaPaymentRepository;
import kr.hhplus.be.server.infra.user.JpaUserCouponRepository;
import kr.hhplus.be.server.infra.user.JpaUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntergrationTest {
    @Autowired
    protected ObjectMapper objectMapper;

    @LocalServerPort
    int port;

    @Autowired
    protected JpaCouponRepository jpaCouponRepository;

    @Autowired
    protected JpaGoodsRepository jpaGoodsRepository;

    @Autowired
    protected JpaGoodsStockRepository jpaGoodsStockRepository;

    @Autowired
    protected JpaSalesHistoryRepository jpaSalesHistoryRepository;

    @Autowired
    protected JpaOrderDetailRepository jpaOrderDetailRepository;

    @Autowired
    protected JpaOrderRepository jpaOrderRepository;

    @Autowired
    protected JpaPaymentRepository jpaPaymentRepository;

    @Autowired
    protected JpaUserCouponRepository jpaUserCouponRepository;

    @Autowired
    protected JpaUserRepository jpaUserRepository;

    @Autowired
    protected UserService userService;

    @Autowired
    protected PaymentService paymentService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected GoodsService goodsService;

    @Autowired
    protected CouponService couponService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @BeforeEach
    void init() {
        jpaCouponRepository.deleteAllInBatch();
        jpaGoodsRepository.deleteAllInBatch();
        jpaGoodsStockRepository.deleteAllInBatch();
        jpaOrderRepository.deleteAllInBatch();
        jpaPaymentRepository.deleteAllInBatch();
        jpaSalesHistoryRepository.deleteAllInBatch();
        jpaOrderDetailRepository.deleteAllInBatch();
        jpaUserCouponRepository.deleteAllInBatch();
        jpaUserRepository.deleteAllInBatch();
    }
}
