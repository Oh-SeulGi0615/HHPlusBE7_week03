package kr.hhplus.be.server.repository;

import kr.hhplus.be.server.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> findByUserId(Long userId);
    Optional<UserEntity> findByUserName(String userName);
    List<UserEntity> findAll();

    UserEntity save(UserEntity userEntity);
}
