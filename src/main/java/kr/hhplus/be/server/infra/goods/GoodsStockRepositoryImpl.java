package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.repository.GoodsStockRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GoodsStockRepositoryImpl implements GoodsStockRepository {
    private final JpaGoodsStockRepository jpaGoodsStockRepository;

    public GoodsStockRepositoryImpl(JpaGoodsStockRepository jpaGoodsStockRepository) {
        this.jpaGoodsStockRepository = jpaGoodsStockRepository;
    }

    @Override
    public Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId) {
        return jpaGoodsStockRepository.findByGoodsStockId(goodsStockId);
    }

    @Override
    public Optional<GoodsStockEntity> findByGoodsId(Long goodsId) {
        return jpaGoodsStockRepository.findByGoodsId(goodsId);
    }

    @Override
    public List<GoodsStockEntity> findAll() {
        return jpaGoodsStockRepository.findAll();
    }

    @Override
    public GoodsStockEntity save(GoodsStockEntity goodsStockEntity) {
        return jpaGoodsStockRepository.save(goodsStockEntity);
    }
}
