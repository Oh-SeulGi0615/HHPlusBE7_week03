package kr.hhplus.be.server.facade;

import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.service.CouponInventoryService;
import kr.hhplus.be.server.domain.coupon.service.CouponQueueService;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import org.springframework.stereotype.Service;

@Service
public class CouponFacade {
    private final CouponService couponService;
    private final CouponInventoryService couponInventoryService;
    private final CouponQueueService couponQueueService;

    public CouponFacade(CouponService couponService, CouponInventoryService couponInventoryService, CouponQueueService couponQueueService) {
        this.couponService = couponService;
        this.couponInventoryService = couponInventoryService;
        this.couponQueueService = couponQueueService;
    }

    public CouponServiceDto issueCoupon(Long userId, Long couponId) {
        CouponEntity couponEntity = couponService.checkValidateCoupon(couponId);
        couponService.checkDuplicateCoupon(userId, couponId);
        couponQueueService.addToQueue(userId, couponId, couponEntity.getCapacity());
        couponInventoryService.decreaseCouponStock(couponId);
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
