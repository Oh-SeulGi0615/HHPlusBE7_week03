package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.payment.PaymentEntity;
import kr.hhplus.be.server.enums.PaymentStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByUserId(Long userId);
    Optional<UserEntity> findByUserName(String userName);
    List<UserEntity> findAll();
    UserEntity save(UserEntity userEntity);

    @Modifying
    @Query("UPDATE UserEntity u SET u.point = :point WHERE u.userId = :userId")
    UserEntity updateUserPoint(@Param("userId") Long userId, @Param("point") Long point);
}
