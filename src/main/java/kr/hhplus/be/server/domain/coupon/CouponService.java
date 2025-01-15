package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.api.request.CreateCouponRequest;
import kr.hhplus.be.server.api.request.GetCouponRequest;
import kr.hhplus.be.server.api.response.CouponResponse;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.enums.UserCouponStatus;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.ExpiredCouponException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidCouponException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        CouponEntity savedCoupon = couponRepository.save(couponEntity);
        return new CouponResponse(
                savedCoupon.getCouponId(),
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

    @Transactional
    public CouponResponse getCoupon(GetCouponRequest getCouponRequest) {
        CouponEntity coupon = couponRepository.findByCouponId(getCouponRequest.getCouponId())
                .orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

        if (userCouponRepository.findByCouponIdAndUserId(getCouponRequest.getCouponId(), getCouponRequest.getUserId()).isPresent()){
            throw new InvalidCouponException("이미 발급받은 쿠폰입니다.");
        }

        if (coupon.getCapacity() < 1) {
            throw new CouponOutOfStockException("쿠폰이 모두 소진되었습니다.");
        }
        coupon.setCapacity(coupon.getCapacity() - 1);

        UserCouponEntity userCoupon = new UserCouponEntity(getCouponRequest.getUserId(), getCouponRequest.getCouponId());
        userCouponRepository.save(userCoupon);

        return new CouponResponse(
                getCouponRequest.getCouponId(),
                coupon.getCouponName(),
                coupon.getDiscountRate(),
                coupon.getCapacity(),
                coupon.getDueDate()
        );
    }

    @Transactional
    public UserCouponResponse useCoupon(GetCouponRequest getCouponRequest) {
       UserCouponEntity myCoupon = userCouponRepository.findByCouponIdAndUserId(
                getCouponRequest.getCouponId(), getCouponRequest.getUserId()
        ).orElseThrow(() -> new InvalidCouponException("존재하지 않는 쿠폰입니다."));

       if (couponRepository.findByCouponId(myCoupon.getCouponId()).get().getDueDate().isBefore(LocalDate.now())) {
           myCoupon.setStatus(UserCouponStatus.EXPIRED);
           throw new ExpiredCouponException("만료된 쿠폰입니다.");
       }

        myCoupon.setStatus(UserCouponStatus.USED);
        return new UserCouponResponse(myCoupon.getUserId(), myCoupon.getCouponId(), myCoupon.isStatus());
    }
}
