package kr.hhplus.be.server.domain.goods;

import java.util.List;
import java.util.Optional;

public interface GoodsRepository {
    Optional<GoodsEntity> findByGoodsId(Long goodsId);
    Optional<GoodsEntity> findByGoodsName(String goodsName);
    List<GoodsEntity> findAll();
    GoodsEntity save(GoodsEntity goodsEntity);
}
