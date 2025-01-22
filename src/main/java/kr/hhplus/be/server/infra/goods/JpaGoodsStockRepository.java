package kr.hhplus.be.server.infra.goods;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaGoodsStockRepository extends JpaRepository<GoodsStockEntity, Long> {
    Optional<GoodsStockEntity> findByGoodsStockId(Long goodsStockId);
    Optional<GoodsStockEntity> findByGoodsId(Long goodsId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from GoodsStockEntity g where g.goodsId = :goodsId")
    Optional<GoodsStockEntity> findByGoodsIdPessimistic(@Param("goodsId") Long goodsId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("select g from GoodsStockEntity g where g.goodsId = :goodsId")
    Optional<GoodsStockEntity> findByGoodsIdOptimistic(@Param("goodsId") Long goodsId);
}
