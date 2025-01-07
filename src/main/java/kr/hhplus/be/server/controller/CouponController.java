package kr.hhplus.be.server.controller;

import kr.hhplus.be.server.domain.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.dto.request.GetCouponRequest;
import kr.hhplus.be.server.domain.dto.response.CouponResponse;
import kr.hhplus.be.server.domain.entity.CouponEntity;
import kr.hhplus.be.server.exeption.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.InvalidCouponException;
import kr.hhplus.be.server.service.CouponService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CouponController {
    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping("/coupons/create")
    public ResponseEntity<Object> createCoupon(@RequestBody CreateCouponRequest createCouponRequest) {
        try {
            CouponResponse response = couponService.createCoupon(createCouponRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidCouponException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/coupons")
    public List<CouponEntity> allCouponList() {
        return couponService.allCouponList();
    }

    @PostMapping("/coupons/{couponId}/get")
    public ResponseEntity<Object> getCoupon(GetCouponRequest getCouponRequest) {
        try {
            CouponResponse response = couponService.getCoupon(getCouponRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidCouponException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (CouponOutOfStockException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
