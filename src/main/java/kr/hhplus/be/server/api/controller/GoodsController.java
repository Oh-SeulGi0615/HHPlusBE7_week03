package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.api.response.GoodsResponse;
import kr.hhplus.be.server.domain.goods.GoodsService;
import kr.hhplus.be.server.domain.goods.SalesHistoryEntity;
import kr.hhplus.be.server.exeption.InvalidCouponException;
import kr.hhplus.be.server.exeption.InvalidGoodsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping("/goods/create")
    public ResponseEntity<Object> createGoods(@RequestBody GoodsRequest goodsRequest) {
        try {
            GoodsResponse response = goodsService.createGoods(goodsRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidGoodsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/goods")
    public List<GoodsResponse> findAllGoods() {
        return goodsService.getAllGoods();
    }

    @GetMapping("/goods/{id}")
    public ResponseEntity<Object> getOneGoodsInfo(@PathVariable("id") Long goodsId) {
        try {
            GoodsResponse response = goodsService.getOneGoodsInfo(goodsId);
            return ResponseEntity.ok(response);
        } catch (InvalidGoodsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/goods/best")
    public List<SalesHistoryEntity> getBestGoods() {
        return goodsService.getBest10Goods();
    }
}
