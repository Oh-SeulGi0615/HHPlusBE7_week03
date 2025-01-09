package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.GoodsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaGoodsRepository extends JpaRepository<GoodsEntity, Long> {
    Optional<GoodsEntity> findByGoodsId(Long goodsId);
    Optional<GoodsEntity> findByGoodsName(String goodsName);
}
