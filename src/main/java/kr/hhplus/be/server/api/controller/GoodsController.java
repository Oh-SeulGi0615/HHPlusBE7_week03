package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.api.response.SalesHistoryResponse;
import kr.hhplus.be.server.domain.goods.dto.GoodsServiceDto;
import kr.hhplus.be.server.domain.goods.entity.SalesHistoryEntity;
import kr.hhplus.be.server.domain.goods.service.GoodsService;
import kr.hhplus.be.server.domain.goods.dto.SalesHistoryServiceDto;
import kr.hhplus.be.server.facade.GoodsFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GoodsController {
    private final GoodsFacade goodsFacade;

    public GoodsController(GoodsFacade goodsFacade) {
        this.goodsFacade = goodsFacade;
    }

    @PostMapping("/goods/create")
    public ResponseEntity<Object> createGoods(@RequestBody GoodsRequest goodsRequest) {
        GoodsServiceDto response = goodsFacade.createGoods(goodsRequest.getGoodsName(), goodsRequest.getPrice(), goodsRequest.getQuantity());
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods")
    public List<GoodsServiceDto> getAllGoods() {
        return goodsFacade.getAllGoods();
    }

    @GetMapping("/goods/{goodsId}")
    public ResponseEntity<Object> getOneGoodsInfo(@PathVariable("goodsId") Long goodsId) {
        GoodsServiceDto response = goodsFacade.getOneGoodsInfo(goodsId);
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods/best")
    public List<SalesHistoryResponse> getBestGoods() {
        List<SalesHistoryEntity> best10Goods = goodsFacade.getCachedBest10Goods();
        return best10Goods.stream().map(SalesHistoryEntity -> new SalesHistoryResponse(
                SalesHistoryEntity.getGoodsId(), SalesHistoryEntity.getUserId(), SalesHistoryEntity.getQuantity()
        )).collect(Collectors.toList());
    }
}
