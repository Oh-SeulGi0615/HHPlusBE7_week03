package kr.hhplus.be.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class GoodsController {

    @GetMapping("/goods")
    public ResponseEntity<Object> findAllGoods() {
        List<Object> goodsList = new ArrayList<>();
        Map<String, Object> goodsInfo = new HashMap<>();
        goodsInfo.put("goodsId", 1L);
        goodsInfo.put("price", 10000L);
        goodsInfo.put("quanttity", 30);

        goodsList.add(goodsInfo);
        return ResponseEntity.ok(goodsList);
    }

    @GetMapping("/goods/{id}")
    public ResponseEntity<Object> findOneGoods(@PathVariable("id") Long goodsId) {
        List<Map<String, Object>> goodsList = new ArrayList<>();

        Map<String, Object> goodsInfo1 = new HashMap<>();
        goodsInfo1.put("goodsId", 1L);
        goodsInfo1.put("price", 10000L);
        goodsInfo1.put("quantity", 30);

        Map<String, Object> goodsInfo2 = new HashMap<>();
        goodsInfo2.put("goodsId", 2L);
        goodsInfo2.put("price", 20000L);
        goodsInfo2.put("quantity", 15);

        goodsList.add(goodsInfo1);
        goodsList.add(goodsInfo2);

        for (Map<String, Object> goods : goodsList) {
            if (goods.get("goodsId").equals(goodsId)) {
                return ResponseEntity.ok(goods);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품 정보를 찾을 수 없습니다.");
    }

    @GetMapping("/goods/best")
    public ResponseEntity<Object> findBestGoods() {
        List<Object> goodsList = new ArrayList<>();
        Map<String, Object> goodsInfo = new HashMap<>();
        goodsInfo.put("goodsId", 1L);
        goodsInfo.put("price", 10000L);
        goodsInfo.put("quanttity", 30);

        goodsList.add(goodsInfo);
        return ResponseEntity.ok(goodsList);
    }
}
