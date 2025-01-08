package kr.hhplus.be.server.domain.goods;

import java.util.List;
import java.util.Optional;

public interface GoodsStockRepository {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);
    List<GoodsStockEntity> findAll();
    GoodsStockEntity save(GoodsStockEntity goodsStockEntity);
}
