package kr.hhplus.be.server.infra.goods;

import kr.hhplus.be.server.domain.goods.SalesHistoryEntity;
import kr.hhplus.be.server.domain.goods.SalesHistoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class SalesHistoryRepositoryImpl implements SalesHistoryRepository {
    private final JpaSalesHistoryRepository jpaSalesHistoryRepository;

    public SalesHistoryRepositoryImpl(JpaSalesHistoryRepository jpaSalesHistoryRepository) {
        this.jpaSalesHistoryRepository = jpaSalesHistoryRepository;
    }

    @Override
    public Optional<SalesHistoryEntity> findBySalesHistoryId(Long salesHistoryId) {
        return jpaSalesHistoryRepository.findBySalesHistoryId(salesHistoryId);
    }

    @Override
    public Optional<SalesHistoryEntity> findByGoodsId(Long goodsId) {
        return jpaSalesHistoryRepository.findByGoodsId(goodsId);
    }

    @Override
    public Optional<SalesHistoryEntity> findByUserId(Long UserID) {
        return jpaSalesHistoryRepository.findByUserId(UserID);
    }

    @Override
    public List<SalesHistoryEntity> findAll() {
        return jpaSalesHistoryRepository.findAll();
    }

    @Override
    public SalesHistoryEntity save(SalesHistoryEntity salesHistoryEntity) {
        return jpaSalesHistoryRepository.save(salesHistoryEntity);
    }

    @Override
    public List<SalesHistoryEntity> findTop10GoodsSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return jpaSalesHistoryRepository.findTop10GoodsSales(startDate, endDate, pageable);
    }
}
