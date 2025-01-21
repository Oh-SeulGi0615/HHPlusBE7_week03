package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.api.response.SalesHistoryResponse;
import kr.hhplus.be.server.domain.goods.GoodsDomainDto;
import kr.hhplus.be.server.domain.goods.GoodsService;
import kr.hhplus.be.server.domain.goods.SalesHistoryDomainDto;
import kr.hhplus.be.server.domain.goods.SalesHistoryEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods/create")
    public ResponseEntity<Object> createGoods(@RequestBody GoodsRequest goodsRequest) {
        GoodsDomainDto response = goodsService.createGoods(goodsRequest.getGoodsName(), goodsRequest.getPrice(), goodsRequest.getQuantity());
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods")
    public List<GoodsDomainDto> getAllGoods() {
        return goodsService.getAllGoods();
    }

    @GetMapping("/goods/{goodsId}")
    public ResponseEntity<Object> getOneGoodsInfo(@PathVariable("goodsId") Long goodsId) {
        GoodsDomainDto response = goodsService.getOneGoodsInfo(goodsId);
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods/best")
    public List<SalesHistoryResponse> getBestGoods() {
        List<SalesHistoryDomainDto> best10Goods = goodsService.getBest10Goods();
        return best10Goods.stream().map(salesHistoryDomainDto -> new SalesHistoryResponse(
                salesHistoryDomainDto.getSalesHistoryId(), salesHistoryDomainDto.getGoodsId(), salesHistoryDomainDto.getUserId(), salesHistoryDomainDto.getQuantity()
        )).collect(Collectors.toList());
    }
}
