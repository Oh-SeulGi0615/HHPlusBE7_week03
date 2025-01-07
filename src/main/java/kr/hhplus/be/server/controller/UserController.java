package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.domain.dto.request.UserRequest;
import kr.hhplus.be.server.domain.dto.request.PointRequest;
import kr.hhplus.be.server.domain.dto.response.UserResponse;
import kr.hhplus.be.server.domain.dto.response.PointResponse;
import kr.hhplus.be.server.exeption.InvalidPointException;
import kr.hhplus.be.server.exeption.InvalidUserException;
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

    @PostMapping("/users/create")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) {
        try {
            UserResponse response = userService.createUser(userRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/users/{id}/points/charge")
    public ResponseEntity<Object> chargePoint(@PathVariable("id") Long userId, @RequestBody Long point) {
        try {
            PointRequest pointRequest = new PointRequest(userId, point);
            PointResponse response = userService.chargePoint(pointRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPointException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/users/{id}/points/check")
    public ResponseEntity<Object> checkPoint(@PathVariable("id") Long userId) {
        try {
            PointResponse response = userService.checkPoint(userId);
            return ResponseEntity.ok(response);
        } catch (InvalidUserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/users/{id}/coupons")
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