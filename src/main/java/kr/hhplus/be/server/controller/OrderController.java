package kr.hhplus.be.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    @PostMapping("/orders")
    public ResponseEntity<Object> orderGoods(Long userId, Long goodsId, int quantity) {
        List<Long> userList = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Map<String, Object>> goodsList = new ArrayList<>();

        Map<String, Object> goodsInfo1 = new HashMap<>();
        goodsInfo1.put("goodsId", 1L);
        goodsInfo1.put("price", 10000L);
        goodsInfo1.put("quantity", 30L);

        Map<String, Object> goodsInfo2 = new HashMap<>();
        goodsInfo2.put("goodsId", 2L);
        goodsInfo2.put("price", 20000L);
        goodsInfo2.put("quantity", 15L);

        goodsList.add(goodsInfo1);
        goodsList.add(goodsInfo2);

        if (!userList.contains(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }

        for (Map<String, Object> goods : goodsList) {
            if (goods.get("goodsId").equals(goodsId)) {
                Long stockQuantity = (Long) goods.get("quantity");
                if (quantity > stockQuantity) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("재고가 부족합니다.");
                }
                goods.put("quantity", stockQuantity - quantity);

                Map<String, Object> order = new HashMap<>();
                order.put("orderId", 1L);
                order.put("orderDate", LocalDateTime.now());
                order.put("userId", userId);
                order.put("goodsId", goodsId);
                order.put("orderedQuantity", quantity);

                Long price = (Long) goods.get("price");
                order.put("totalPrice", quantity * price);
                return ResponseEntity.ok(order);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
    }
}
