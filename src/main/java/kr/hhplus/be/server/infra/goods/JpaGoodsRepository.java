package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.GoodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaGoodsRepository extends JpaRepository<GoodsEntity, Long> {
    Optional<GoodsEntity> findByGoodsId(Long goodsId);
    Optional<GoodsEntity> findByGoodsName(String goodsName);
}
