package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.GoodsStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaGoodsStockRepository extends JpaRepository<GoodsStockEntity, Long> {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);

    @Modifying
    @Query("UPDATE GoodsStockEntity g SET g.quantity = :quantity WHERE g.goodsId = :goodsId")
    GoodsStockEntity updateGoodsStockStatus(@Param("goodsId") Long goodsId, @Param("quantity") Long quantity);
}
