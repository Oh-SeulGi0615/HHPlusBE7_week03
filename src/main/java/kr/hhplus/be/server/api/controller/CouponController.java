package kr.hhplus.be.server.api.controller;

import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.facade.CouponFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponFacade couponFacade;

    public CouponController(CouponFacade couponFacade) {
        this.couponFacade = couponFacade;
    }

    @PostMapping("/coupons/create")
    public ResponseEntity<Object> createCoupon(@RequestBody CreateCouponRequest createCouponRequest) {

        CouponServiceDto response = couponFacade.createCoupon(
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
        List<CouponServiceDto> couponList = couponFacade.allCouponList();
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
    public ResponseEntity<Object> requestCoupon(@PathVariable("couponId") Long couponId, @RequestBody Long userId) {
        CouponServiceDto response = couponFacade.requestCoupon(userId, couponId);

        CouponResponse couponResponse = new CouponResponse(
                response.getCouponId(),
                response.getCouponName(),
                response.getDiscountRate(),
                response.getCapacity(),
                response.getDueDate()
        );
        return ResponseEntity.ok(couponResponse);
    }
}
