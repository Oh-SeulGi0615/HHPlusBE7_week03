package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.api.response.SalesHistoryResponse;
import kr.hhplus.be.server.domain.goods.GoodsServiceDto;
import kr.hhplus.be.server.domain.goods.GoodsService;
import kr.hhplus.be.server.domain.goods.SalesHistoryServiceDto;
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
        GoodsServiceDto response = goodsService.createGoods(goodsRequest.getGoodsName(), goodsRequest.getPrice(), goodsRequest.getQuantity());
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods")
    public List<GoodsServiceDto> getAllGoods() {
        return goodsService.getAllGoods();
    }

    @GetMapping("/goods/{goodsId}")
    public ResponseEntity<Object> getOneGoodsInfo(@PathVariable("goodsId") Long goodsId) {
        GoodsServiceDto response = goodsService.getOneGoodsInfo(goodsId);
        GoodsResponse goodsResponse = new GoodsResponse(response.getGoodsId(), response.getGoodsName(), response.getPrice(), response.getQuantity());
        return ResponseEntity.ok(goodsResponse);
    }

    @GetMapping("/goods/best")
    public List<SalesHistoryResponse> getBestGoods() {
        List<SalesHistoryServiceDto> best10Goods = goodsService.getBest10Goods();
        return best10Goods.stream().map(salesHistoryServiceDto -> new SalesHistoryResponse(
                salesHistoryServiceDto.getSalesHistoryId(), salesHistoryServiceDto.getGoodsId(), salesHistoryServiceDto.getUserId(), salesHistoryServiceDto.getQuantity()
        )).collect(Collectors.toList());
    }
}
