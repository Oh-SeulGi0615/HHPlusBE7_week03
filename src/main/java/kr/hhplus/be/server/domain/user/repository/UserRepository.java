package kr.hhplus.be.server.domain.user.repository;

import kr.hhplus.be.server.domain.user.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByUserId(Long userId);
    Optional<UserEntity> findByUserName(String userName);
    List<UserEntity> findAll();
    UserEntity save(UserEntity userEntity);
}
