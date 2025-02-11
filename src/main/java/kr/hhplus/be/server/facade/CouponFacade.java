package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.service.CouponInventoryService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CouponFacade {
    private final CouponService couponService;
    private final CouponInventoryService couponInventoryService;

    public CouponFacade(CouponService couponService, CouponInventoryService couponInventoryService) {
        this.couponService = couponService;
        this.couponInventoryService = couponInventoryService;
    }

    public CouponServiceDto createCoupon(String couponName, Long discountRate, Long capacity, LocalDate dueDate) {
        return couponService.createCoupon(couponName, discountRate, capacity, dueDate);
    }

    public List<CouponServiceDto> allCouponList() {
        return couponService.allCouponList();
    }

    public CouponServiceDto requestCoupon(Long userId, Long couponId) {
        CouponEntity couponEntity = couponService.checkValidateCoupon(couponId);
        couponService.checkDuplicateCoupon(userId, couponId);
        couponInventoryService.enqueueUser(couponId, userId);
        return new CouponServiceDto(
                couponId,
                couponEntity.getCouponName(),
                couponEntity.getDiscountRate(),
                couponEntity.getCapacity(),
                couponEntity.getDueDate()
        );
    }

    public boolean isCouponIssued(Long userId, Long couponId) {
        return couponService.isCouponIssued(userId, couponId);
    }
}
