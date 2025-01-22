package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.domain.coupon.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/coupons/create")
    public ResponseEntity<Object> createCoupon(@RequestBody CreateCouponRequest createCouponRequest) {

        CouponServiceDto response = couponService.createCoupon(
                createCouponRequest.getCouponName(),
                createCouponRequest.getDiscountRate(),
                createCouponRequest.getCapacity(),
                createCouponRequest.getDueDate()
        );

        CouponResponse couponResponse = new  CouponResponse(
                response.getCouponId(),
                response.getCouponName(),
                response.getDiscountRate(),
                response.getCapacity(),
                response.getDueDate()
        );

        return ResponseEntity.ok(couponResponse);
    }

    @GetMapping("/coupons")
    public List<CouponResponse> allCouponList() {
        List<CouponServiceDto> couponList = couponService.allCouponList();
        return couponList.stream()
                .map(coupon -> new CouponResponse(
                        coupon.getCouponId(),
                        coupon.getCouponName(),
                        coupon.getDiscountRate(),
                        coupon.getCapacity(),
                        coupon.getDueDate()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/coupons/{couponId}/get")
    public ResponseEntity<Object> issueCoupon(@PathVariable("couponId") Long couponId, @RequestBody Long userId) {
        CouponServiceDto response = couponService.issueCoupon(userId, couponId);

        CouponResponse couponResponse = new  CouponResponse(
                response.getCouponId(),
                response.getCouponName(),
                response.getDiscountRate(),
                response.getCapacity(),
                response.getDueDate()
        );
        return ResponseEntity.ok(couponResponse);
    }
}
