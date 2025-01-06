package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.domain.dto.PointChargeResponse;
import kr.hhplus.be.server.exeption.InvalidPointException;
import kr.hhplus.be.server.exeption.UserNotFoundException;
import kr.hhplus.be.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/{id}/points/charge")
    public ResponseEntity<Object> chargePoint(@PathVariable("id") Long userId, Long point) {
        try {
            PointChargeResponse response = userService.chargePoint(userId, point);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPointException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/users/{id}/points/check")
    public ResponseEntity<Object> checkPoint(@PathVariable("id") Long userId) {
        if (userId > 1000000) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("point", 100000L);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/coupons/get")
    public ResponseEntity<Object> getCoupon(@PathVariable("id") Long userId, Long couponId) {
        List<Long> couponList = new ArrayList<>(List.of(1L, 2L, 3L));

        if (userId > 1000000) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }
        if (couponId > 1000000) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("쿠폰 정보를 찾을 수 없습니다.");
        }
        if (!couponList.contains(couponId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 발급된 쿠폰입니다.");
        }
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> couponState = new HashMap<>();

        couponState.put("couponId", couponId);
        couponState.put("state", false);

        response.put("userID", userId);
        response.put("coupon", couponState);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}/coupons/check")
    public ResponseEntity<Object> checkCoupon(@PathVariable("id") Long userId) {
        Long userId1 = 1L;
        List<Long> couponList = new ArrayList<>(List.of(1L, 2L, 3L));
        Map<Long, List<Long>> userInfo = new HashMap<>(Map.of(userId1, couponList));

        if (!userInfo.containsKey(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다.");
        }

        List<Long> userCoupons = userInfo.get(userId);
        return ResponseEntity.ok(userCoupons);
    }
}