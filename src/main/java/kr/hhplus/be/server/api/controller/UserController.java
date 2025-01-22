package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.UserRequest;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.api.response.UserResponse;
import kr.hhplus.be.server.api.response.PointResponse;
import kr.hhplus.be.server.domain.coupon.UserCouponServiceDto;
import kr.hhplus.be.server.domain.user.PointServiceDto;
import kr.hhplus.be.server.domain.user.UserServiceDto;
import kr.hhplus.be.server.domain.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/create")
    public ResponseEntity<Object> createUser(@RequestBody UserRequest userRequest) {
        UserServiceDto response = userService.createUser(userRequest.getName());
        UserResponse userResponse = new UserResponse(
                response.getUserId(), response.getUserName(), response.getPoint()
        );
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/users/{userId}/points/charge")
    public ResponseEntity<Object> chargePoint(@PathVariable("userId") Long userId, @RequestBody Long point) {
        PointServiceDto response = userService.chargePoint(userId, point);
        PointResponse pointResponse = new PointResponse(response.getUserId(), response.getPoint());
        return ResponseEntity.ok(pointResponse);
    }

    @GetMapping("/users/{userId}/points/check")
    public ResponseEntity<Object> checkPoint(@PathVariable("userId") Long userId) {
        PointServiceDto response = userService.checkPoint(userId);
        PointResponse pointResponse = new PointResponse(response.getUserId(), response.getPoint());
        return ResponseEntity.ok(pointResponse);
    }


    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<Object> checkCoupon(@PathVariable("userId") Long userId) {
        List<UserCouponServiceDto> response = userService.checkAllMyCoupon(userId);
        List<UserCouponResponse> couponResponses = response.stream().map(userCouponServiceDto -> new UserCouponResponse(
                userCouponServiceDto.getUserId(),
                userCouponServiceDto.getCouponId(),
                userCouponServiceDto.getStatus()
        )).collect(Collectors.toList());;
        return ResponseEntity.ok(couponResponses);
    }
}