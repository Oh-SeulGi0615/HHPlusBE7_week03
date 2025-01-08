package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.GoodsStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaGoodsStockRepository extends JpaRepository<GoodsStockEntity, Long> {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);
}
