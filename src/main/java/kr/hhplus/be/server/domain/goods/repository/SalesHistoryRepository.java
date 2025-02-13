package kr.hhplus.be.server.domain.goods.repository;

import kr.hhplus.be.server.domain.goods.entity.SalesHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SalesHistoryRepository {
    Optional<SalesHistoryEntity> findBySalesHistoryId(Long salesHistoryId);
    Optional<SalesHistoryEntity> findByGoodsId(Long goodsId);
    Optional<SalesHistoryEntity> findByUserId(Long UserID);
    List<SalesHistoryEntity> findAll();
    SalesHistoryEntity save(SalesHistoryEntity salesHistoryEntity);

    @Query("""
    SELECT s.goodsId AS goodsId,
           SUM(s.quantity) AS totalQuantity
    FROM SalesHistoryEntity s
    WHERE s.createdAt BETWEEN :startDate AND :endDate
    GROUP BY s.goodsId
    ORDER BY SUM(s.quantity) DESC
    LIMIT 10
    """)
    List<SalesHistoryEntity> findTop10GoodsSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
