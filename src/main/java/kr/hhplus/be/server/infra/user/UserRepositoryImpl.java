package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.user.entity.UserEntity;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
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
        return jpaUserRepository.save(userEntity);
    }

    @Override
    public UserEntity saveAndFlush(UserEntity userEntity) {
        return jpaUserRepository.saveAndFlush(userEntity);
    }
}
