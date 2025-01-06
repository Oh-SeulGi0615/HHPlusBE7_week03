package kr.hhplus.be.server.service;

import kr.hhplus.be.server.domain.dto.PointChargeResponse;
import kr.hhplus.be.server.domain.entity.UserEntity;
import kr.hhplus.be.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public PointChargeResponse chargePoint(Long userId, Long point) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("유저를 찾을 수 없습니다.")
        );
        Long currentPoint = userEntity.getPoint();

        if (point < 0 || point % 10 > 0){
            throw new IllegalArgumentException("충전 가능한 금액은 0원 초과 10원 단위입니다.");
        }

        if (point > 1000000) {
            throw new IllegalArgumentException("1회 충전 가능한 금액은 최대 1,000,000원 입니다.");
        }

        if (point + currentPoint > 10000000) {
            throw new IllegalArgumentException("보유할 수 있는 최대 금액은 10,000,000원 입니다.");
        }

        Long updatedPoint = currentPoint + point;
        userEntity.setPoint(updatedPoint);
        userRepository.save(userEntity);
        return new PointChargeResponse(userId, updatedPoint);
    }
}
