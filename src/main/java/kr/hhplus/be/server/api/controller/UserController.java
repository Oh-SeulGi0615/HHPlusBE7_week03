package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.UserRequest;
import kr.hhplus.be.server.api.request.PointRequest;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.api.response.UserResponse;
import kr.hhplus.be.server.api.response.PointResponse;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/create")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) {
        UserResponse response = userService.createUser(userRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/points/charge")
    public ResponseEntity<Object> chargePoint(@PathVariable("userId") Long userId, @RequestBody Long point) {
        PointRequest pointRequest = new PointRequest(userId, point);
        PointResponse response = userService.chargePoint(pointRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/points/check")
    public ResponseEntity<Object> checkPoint(@PathVariable("userId") Long userId) {
        PointResponse response = userService.checkPoint(userId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<Object> checkCoupon(@PathVariable("userId") Long userId) {
        List<UserCouponResponse> response = userService.checkAllMyCoupon(userId);
        return ResponseEntity.ok(response);
    }
}