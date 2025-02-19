package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.api.request.GetCouponRequest;
import kr.hhplus.be.server.api.response.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.dto.CouponServiceDto;
import kr.hhplus.be.server.domain.coupon.entity.CouponEntity;
import kr.hhplus.be.server.domain.coupon.entity.UserCouponEntity;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.exeption.customExceptions.CouponOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.ExistCouponException;
import kr.hhplus.be.server.exeption.customExceptions.ExpiredCouponException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void 신규쿠폰생성_성공케이스() {
        // given
        String couponName = "TestCoupon";
        Long discountRate = 20L;
        Long capacity = 100L;
        LocalDate dueDate = LocalDate.now().plusDays(10);

        CouponEntity couponEntity = new CouponEntity(couponName, discountRate, capacity, dueDate);

        when(couponRepository.findByCouponName(couponName)).thenReturn(Optional.empty());
        when(couponRepository.save(any(CouponEntity.class))).thenReturn(couponEntity);

        // when
        CouponServiceDto response = couponService.createCoupon(couponName, discountRate, capacity, dueDate);

        // then
        assertEquals("TestCoupon", response.getCouponName());
        assertEquals(20L, response.getDiscountRate());
        assertEquals(100L, response.getCapacity());
    }

    @Test
    void 신규쿠폰생성_중복쿠폰_실패케이스() {
        // given
        String couponName = "DuplicateCoupon";
        Long discountRate = 20L;
        Long capacity = 100L;
        LocalDate dueDate = LocalDate.now().plusDays(10);
        CouponEntity existingCoupon = new CouponEntity(couponName, discountRate, capacity, dueDate);

        when(couponRepository.findByCouponName(couponName)).thenReturn(Optional.of(existingCoupon));

        // when
        Exception exception = assertThrows(ExistCouponException.class, () -> couponService.createCoupon(couponName, discountRate, capacity, dueDate));

        // then
        assertEquals("이미 등록된 쿠폰입니다.", exception.getMessage());
    }

    @Test
    void 전체쿠폰리스트조회_성공케이스() {
        // given
        CouponEntity coupon1 = new CouponEntity("Coupon1", 10L, 50L, LocalDate.now().plusDays(5));
        CouponEntity coupon2 = new CouponEntity("Coupon2", 20L, 100L, LocalDate.now().plusDays(10));
        when(couponRepository.findAll()).thenReturn(List.of(coupon1, coupon2));

        // when
        List<CouponServiceDto> result = couponService.allCouponList();

        // then
        assertEquals(2, result.size());
    }

    @Test
    void 쿠폰발급_성공케이스() {
        // given
        GetCouponRequest request = new GetCouponRequest(1L, 100L);
        CouponEntity coupon = new CouponEntity("TestCoupon", 10L, 5L, LocalDate.now().plusDays(5));

        when(couponRepository.findByCouponId(request.getCouponId())).thenReturn(Optional.of(coupon));
        when(userCouponRepository.save(any(UserCouponEntity.class))).thenReturn(new UserCouponEntity(1L, 100L));

        // when
        CouponServiceDto response = couponService.issueCoupon(request.getUserId(), request.getUserId());

        // then
        assertEquals("TestCoupon", response.getCouponName());
        assertEquals(4L, response.getCapacity());
    }

    @Test
    void 쿠폰발급_재고소진_실패케이스() {
        // given
        GetCouponRequest request = new GetCouponRequest(1L, 100L);
        CouponEntity coupon = new CouponEntity("TestCoupon", 10L, 0L, LocalDate.now().plusDays(5));

        when(couponRepository.findByCouponId(request.getCouponId())).thenReturn(Optional.of(coupon));

        // when
        Exception exception = assertThrows(CouponOutOfStockException.class, () -> couponService.issueCoupon(request.getUserId(), request.getUserId()));

        // then
        assertEquals("쿠폰이 모두 소진되었습니다.", exception.getMessage());
    }

    @Test
    void 쿠폰사용_성공케이스() {
        // given
        GetCouponRequest request = new GetCouponRequest(1L, 1L);
        UserCouponEntity userCoupon = new UserCouponEntity(1L, 1L);
        CouponEntity coupon = new CouponEntity("TestCoupon", 10L, 5L, LocalDate.now().plusDays(5));

        when(userCouponRepository.findByCouponIdAndUserId(request.getCouponId(), request.getUserId())).thenReturn(Optional.of(userCoupon));
        when(couponRepository.findByCouponId(request.getCouponId())).thenReturn(Optional.of(coupon));

        // when
        UserCouponResponse response = couponService.useCoupon(request);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getCouponId());
    }

    @Test
    void 쿠폰사용_만료된쿠폰_실패케이스() {
        // given
        GetCouponRequest request = new GetCouponRequest(1L, 1L);
        UserCouponEntity userCoupon = new UserCouponEntity(1L, 1L);
        CouponEntity expiredCoupon = new CouponEntity("ExpiredCoupon", 10L, 5L, LocalDate.now().minusDays(1));

        when(userCouponRepository.findByCouponIdAndUserId(request.getCouponId(), request.getUserId()))
                .thenReturn(Optional.of(userCoupon));
        when(couponRepository.findByCouponId(request.getCouponId()))
                .thenReturn(Optional.of(expiredCoupon));

        // when
        Exception exception = assertThrows(ExpiredCouponException.class, () -> couponService.useCoupon(request));

        // then
        assertEquals("만료된 쿠폰입니다.", exception.getMessage());
    }
}