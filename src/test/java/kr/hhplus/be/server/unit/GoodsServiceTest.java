package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.domain.goods.*;
import kr.hhplus.be.server.exeption.customExceptions.InvalidGoodsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {
    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private GoodsStockRepository goodsStockRepository;

    @Mock
    private SalesHistoryRepository salesHistoryRepository;

    @InjectMocks
    private GoodsService goodsService;

    @Test
    void 상품정보생성_성공케이스() {
        // given
        GoodsRequest goodsRequest = new GoodsRequest("Test Goods", 1000L, 10L);
        GoodsEntity savedGoods = new GoodsEntity("Test Goods", 1000L);
        GoodsStockEntity savedGoodsStock = new GoodsStockEntity(1L, 1000L);

        when(goodsRepository.findByGoodsName("Test Goods")).thenReturn(Optional.empty());
        when(goodsRepository.save(any(GoodsEntity.class))).thenReturn(savedGoods);
        when(goodsStockRepository.save(any(GoodsStockEntity.class))).thenReturn(savedGoodsStock);

        // when
        GoodsResponse response = goodsService.createGoods(goodsRequest);

        // then
        assertNotNull(response);
        assertEquals("Test Goods", response.getGoodsName());
        assertEquals(1000L, response.getPrice());
        assertEquals(10L, response.getQuantity());
    }

    @Test
    void 상품정보생성_중복상품_실패케이스() {
        // given
        GoodsRequest goodsRequest = new GoodsRequest("Duplicate Goods", 1500L, 5L);
        GoodsEntity existingGoods = new GoodsEntity("Duplicate Goods", 1500L);

        when(goodsRepository.findByGoodsName("Duplicate Goods")).thenReturn(Optional.of(existingGoods));

        // when & then
        Exception exception = assertThrows(InvalidGoodsException.class, () -> goodsService.createGoods(goodsRequest));
        assertEquals("이미 등록된 상품입니다.", exception.getMessage());
    }

    @Test
    void 모든상품조회_성공케이스() {
        // given
        GoodsEntity goods1 = new GoodsEntity("Goods 1", 500L);
        GoodsEntity goods2 = new GoodsEntity("Goods 2", 1500L);
        when(goodsRepository.findAll()).thenReturn(Arrays.asList(goods1, goods2));

        // when
        List<GoodsResponse> responses = goodsService.getAllGoods();

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Goods 1", responses.get(0).getGoodsName());
        assertEquals("Goods 2", responses.get(1).getGoodsName());
    }

    @Test
    void 특정상품조회_성공케이스() {
        // given
        Long goodsId = 1L;
        GoodsEntity goods = new GoodsEntity("Goods 1", 500L);
        when(goodsRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goods));
        GoodsStockEntity stock = new GoodsStockEntity(goodsId, 20L);
        when(goodsStockRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(stock));

        // when
        GoodsResponse response = goodsService.getOneGoodsInfo(goodsId);

        // then
        assertNotNull(response);
        assertEquals("Goods 1", response.getGoodsName());
        assertEquals(500L, response.getPrice());
        assertEquals(20L, response.getQuantity());
    }

    @Test
    void 특정상품조회_없는상품조회_실패케이스() {
        // given
        Long nonExistentGoodsId = 999L;

        when(goodsRepository.findByGoodsId(nonExistentGoodsId)).thenReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(InvalidGoodsException.class, () -> goodsService.getOneGoodsInfo(nonExistentGoodsId));
        assertEquals("상품 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 인기상품조회_성공케이스() {
        // given
        when(salesHistoryRepository.findTop10GoodsSales(any(), any(), any())).thenReturn(Arrays.asList(
                new SalesHistoryEntity(1L, 1L, 10L),
                new SalesHistoryEntity(2L, 2L, 20L),
                new SalesHistoryEntity(3L, 3L, 30L),
                new SalesHistoryEntity(4L, 4L, 40L),
                new SalesHistoryEntity(5L, 5L, 50L)
        ));

        // when
        List<SalesHistoryEntity> result = goodsService.getBest10Goods();

        // then
        assertNotNull(result);
        assertEquals(5, result.size());
    }
}