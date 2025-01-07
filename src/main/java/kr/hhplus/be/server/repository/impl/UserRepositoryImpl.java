package kr.hhplus.be.server.repository.impl;

import kr.hhplus.be.server.domain.entity.UserEntity;
import kr.hhplus.be.server.repository.UserRepository;
import kr.hhplus.be.server.repository.jpa.JpaUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImpl(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public Optional<UserEntity> findByUserId(Long userId) {
        return jpaUserRepository.findById(userId);
    }

    @Override
    public Optional<UserEntity> findByUserName(String userName) {
        return jpaUserRepository.findByUserName(userName);
    }

    @Override
    public List<UserEntity> findAll() {
        return jpaUserRepository.findAll();
    }

    @Override
    public UserEntity save(UserEntity userEntity) {
        jpaUserRepository.save(userEntity);
        return userEntity;
    }
}
