package kr.hhplus.be.server.domain.goods;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GoodsStockRepository {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);
    List<GoodsStockEntity> findAll();
    GoodsStockEntity save(GoodsStockEntity goodsStockEntity);

    @Modifying
    @Query("UPDATE GoodsStockEntity g SET g.quantity = :quantity WHERE g.goodsId = :goodsId")
    GoodsStockEntity updateGoodsStock(@Param("goodsId") Long goodsId, @Param("quantity") Long quantity);
}
