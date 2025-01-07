package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.dto.request.CreateCouponRequest;
import kr.hhplus.be.server.domain.dto.request.GetCouponRequest;
import kr.hhplus.be.server.domain.dto.response.CouponResponse;
import kr.hhplus.be.server.domain.entity.CouponEntity;
import kr.hhplus.be.server.domain.entity.UserCouponEntity;
import kr.hhplus.be.server.exeption.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.InvalidCouponException;
import kr.hhplus.be.server.repository.CouponRepository;
import kr.hhplus.be.server.repository.UserCouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public CouponService(CouponRepository couponRepository, UserCouponRepository userCouponRepository) {
        this.couponRepository = couponRepository;
        this.userCouponRepository = userCouponRepository;
    }

    public CouponResponse createCoupon(CreateCouponRequest createCouponRequest) {
        if (couponRepository.findByCouponName(createCouponRequest.getCouponName()).isPresent()) {
            throw new InvalidCouponException("이미 등록된 쿠폰입니다.");
        }
        CouponEntity couponEntity = new CouponEntity(
                createCouponRequest.getCouponName(),
                createCouponRequest.getDiscountRate(),
                createCouponRequest.getCapacity(),
                createCouponRequest.getDueDate()
        );
        couponRepository.save(couponEntity);
        Long couponId = couponRepository.findByCouponName(createCouponRequest.getCouponName()).get().getCouponId();
        return new CouponResponse(
                couponId,
                createCouponRequest.getCouponName(),
                createCouponRequest.getDiscountRate(),
                createCouponRequest.getCapacity(),
                createCouponRequest.getDueDate()
        );
    }

    public List<CouponEntity> allCouponList() {
        List<CouponEntity> couponList = couponRepository.findAll();
        return couponList;
    }

    public CouponResponse getCoupon(GetCouponRequest getCouponRequest) {
        CouponEntity coupon = couponRepository.findByCouponId(getCouponRequest.getCouponId())
                .orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

        if (coupon.getCapacity() < 1) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
        couponRepository.decrementCapacity(getCouponRequest.getCouponId());

        UserCouponEntity userCoupon = new UserCouponEntity(getCouponRequest.getUserId(), getCouponRequest.getCouponId());
        userCouponRepository.save(userCoupon);

        return new CouponResponse(
                getCouponRequest.getCouponId(),
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity() - 1,
                coupon.getDueDate()
        );
    }
}
