package kr.hhplus.be.server.service;

import kr.hhplus.be.server.repository.CouponRepository;
import kr.hhplus.be.server.repository.UserCouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    void createCoupon() {
        //given


        //when


        //then


    }

    @Test
    void allCouponList() {
        //given


        //when


        //then


    }

    @Test
    void getCoupon() {
        //given


        //when


        //then


    }
}