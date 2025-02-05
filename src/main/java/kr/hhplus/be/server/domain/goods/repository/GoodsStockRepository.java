package kr.hhplus.be.server.domain.goods.repository;

import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;

import java.util.List;
import java.util.Optional;

public interface GoodsStockRepository {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);
    List<GoodsStockEntity> findAll();
    GoodsStockEntity save(GoodsStockEntity goodsStockEntity);
}
