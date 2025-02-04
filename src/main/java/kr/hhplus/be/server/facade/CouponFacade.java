package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.service.CouponInventoryService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import org.springframework.stereotype.Service;

@Service
public class CouponFacade {
    private final CouponService couponService;
    private final CouponInventoryService couponInventoryService;

    public CouponFacade(CouponService couponService, CouponInventoryService couponInventoryService) {
        this.couponService = couponService;
        this.couponInventoryService = couponInventoryService;
    }

    public CouponServiceDto issueCoupon(Long userId, Long couponId) {
        CouponEntity couponEntity = couponService.checkValidateCoupon(couponId);
        couponService.checkDuplicateCoupon(userId, couponId);
        couponInventoryService.issueCoupon(couponId, couponEntity.getCapacity(), userId);
        CouponEntity issuedCoupon = couponService.updateCouponInfo(userId, couponId);
        return new CouponServiceDto(
                couponId,
                issuedCoupon.getCouponName(),
                issuedCoupon.getDiscountRate(),
                issuedCoupon.getCapacity(),
                issuedCoupon.getDueDate()
        );
    }
}
