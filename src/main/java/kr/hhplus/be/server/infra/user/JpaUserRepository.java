package kr.hhplus.be.server.infra.user;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.user.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserName(String userName);

    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("select u from UserEntity u where u.userId = :userId")
    Optional<UserEntity> findByUserId(@Param("userId") Long userId);
}
