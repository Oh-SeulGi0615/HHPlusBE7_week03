package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.domain.goods.GoodsService;
import kr.hhplus.be.server.domain.goods.SalesHistoryEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods/create")
    public ResponseEntity<Object> createGoods(@RequestBody GoodsRequest goodsRequest) {
        GoodsResponse response = goodsService.createGoods(goodsRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goods")
    public List<GoodsResponse> getAllGoods() {
        return goodsService.getAllGoods();
    }

    @GetMapping("/goods/{goodsId}")
    public ResponseEntity<Object> getOneGoodsInfo(@PathVariable("goodsId") Long goodsId) {
        GoodsResponse response = goodsService.getOneGoodsInfo(goodsId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goods/best")
    public List<SalesHistoryEntity> getBestGoods() {
        return goodsService.getBest10Goods();
    }
}
