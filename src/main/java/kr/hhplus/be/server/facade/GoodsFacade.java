package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.goods.dto.GoodsServiceDto;
import kr.hhplus.be.server.domain.goods.dto.SalesHistoryServiceDto;
import kr.hhplus.be.server.domain.goods.service.GoodsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsFacade {
    private final GoodsService goodsService;

    public GoodsFacade(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    public GoodsServiceDto createGoods(String goodsName, Long price, Long quantity) {
        return goodsService.createGoods(goodsName, price, quantity);
    }

    public List<GoodsServiceDto> getAllGoods() {
        return goodsService.getAllGoods();
    }

    public GoodsServiceDto getOneGoodsInfo(Long goodsId) {
        return goodsService.getOneGoodsInfo(goodsId);
    }

    public List<SalesHistoryServiceDto> getBest10Goods() {
        return goodsService.getBest10Goods();
    }
}
