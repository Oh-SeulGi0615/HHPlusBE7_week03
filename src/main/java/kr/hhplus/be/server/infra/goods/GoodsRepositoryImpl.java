package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.GoodsEntity;
import kr.hhplus.be.server.domain.goods.GoodsRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GoodsRepositoryImpl implements GoodsRepository {
    private final JpaGoodsRepository jpaGoodsRepository;

    public GoodsRepositoryImpl(JpaGoodsRepository jpaGoodsRepository) {
        this.jpaGoodsRepository = jpaGoodsRepository;
    }

    @Override
    public Optional<GoodsEntity> findByGoodsId(Long goodsId) {
        return jpaGoodsRepository.findByGoodsId(goodsId);
    }

    @Override
    public Optional<GoodsEntity> findByGoodsName(String goodsName) {
        return jpaGoodsRepository.findByGoodsName(goodsName);
    }

    @Override
    public List<GoodsEntity> findAll() {
        return jpaGoodsRepository.findAll();
    }

    @Override
    public GoodsEntity save(GoodsEntity goodsEntity) {
        return jpaGoodsRepository.save(goodsEntity);
    }
}
