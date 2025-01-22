package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.domain.user.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 신규유저_생성시_성공케이스() {
        //given
        final String userName = "test";

        UserEntity userEntity = new UserEntity(userName);
        userEntity.setUserId(1L);
        userEntity.setPoint(0L);

        when(userRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        //when
        UserServiceDto response = userService.createUser(userName);

        //then
        assertThat(response).isNotNull();
        assertThat(response.getUserName()).isEqualTo(userName);
        assertThat(response.getPoint()).isEqualTo(0L);
        assertThat(response.getUserId()).isEqualTo(1L);
    }

    @Test
    void 신규유저_생성시_중복가입_오류케이스() {
        // given
        final String userName = "testUser";
        when(userRepository.findByUserName(userName)).thenReturn(Optional.of(new UserEntity(userName)));

        // when, then
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    UserServiceDto response = userService.createUser(userName);
                }
        );

        assertThat(exception.getMessage()).isEqualTo("이미 등록된 유저입니다.");
    }

    @Test
    void 전체유저조회_성공케이스() {
        //given
        List<UserEntity> users = Arrays.asList(
                new UserEntity("user1"),
                new UserEntity("user2")
        );
        when(userRepository.findAll()).thenReturn(users);

        //when
        List<UserEntity> result = userService.getAllUser();

        //then
        assertNotNull(result);
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void 포인트충전_성공케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(1000L);

        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        //when
        PointServiceDto response = userService.chargePoint(1L, 500L);

        //then
        assertNotNull(response);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getPoint()).isEqualTo(1500L);
    }

    @Test
    void 포인트충전_유저조회불가_실패케이스() {
        //given
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());

        //when
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.chargePoint(1L, 500L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("유저를 찾을 수 없습니다.");
    }

    @Test
    void 포인트충전_마이너스포인트충전_실패케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(1000L);

        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        //when
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.chargePoint(userEntity.getUserId(), -10L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("충전 가능한 금액은 0원 초과 10원 단위입니다.");
    }

    @Test
    void 포인트충전_1원단위_포인트충전_실패케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(1000L);

        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        //when
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.chargePoint(userEntity.getUserId(), 13L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("충전 가능한 금액은 0원 초과 10원 단위입니다.");
    }

    @Test
    void 포인트충전_1회충전_한도금액초과_실패케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(1000L);

        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        //when
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.chargePoint(userEntity.getUserId(), 100_000_000L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
    }

    @Test
    void 포인트충전_보유포인트_한도금액초과_실패케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(9_900_000L);

        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        //when
        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.chargePoint(userEntity.getUserId(), 1_000_000L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("보유할 수 있는 최대 금액은 10,000,000원 입니다.");
    }

    @Test
    void 보유포인트조회_성공케이스() {
        //given
        UserEntity userEntity = new UserEntity("testUser");
        userEntity.setUserId(1L);
        userEntity.setPoint(2000L);

        when(userRepository.findByUserId(1L)).thenReturn(Optional.of(userEntity));

        //when
        PointServiceDto response = userService.checkPoint(1L);

        //then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(2000L, response.getPoint());
    }

    @Test
    void 보유포인트조회_유저조회_실패케이스() {
        when(userRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(
                Exception.class,
                () -> {
                    PointServiceDto response = userService.checkPoint(1L);
                }
        );

        //then
        assertThat(exception.getMessage()).isEqualTo("유저를 찾을 수 없습니다.");
    }
}
