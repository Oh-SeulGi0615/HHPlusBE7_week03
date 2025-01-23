package kr.hhplus.be.server.domain.goods.service;

import kr.hhplus.be.server.domain.goods.dto.GoodsServiceDto;
import kr.hhplus.be.server.domain.goods.dto.SalesHistoryServiceDto;
import kr.hhplus.be.server.domain.goods.entity.GoodsEntity;
import kr.hhplus.be.server.domain.goods.entity.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.entity.SalesHistoryEntity;
import kr.hhplus.be.server.domain.goods.repository.GoodsRepository;
import kr.hhplus.be.server.domain.goods.repository.GoodsStockRepository;
import kr.hhplus.be.server.domain.goods.repository.SalesHistoryRepository;
import kr.hhplus.be.server.exeption.customExceptions.ExistGoodsException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidGoodsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    private final GoodsRepository goodsRepository;
    private final SalesHistoryRepository salesHistoryRepository;
    private final GoodsStockRepository goodsStockRepository;

    @Autowired
    public GoodsService(GoodsRepository goodsRepository, SalesHistoryRepository salesHistoryRepository, GoodsStockRepository goodsStockRepository) {
        this.goodsRepository = goodsRepository;
        this.salesHistoryRepository = salesHistoryRepository;
        this.goodsStockRepository = goodsStockRepository;
    }

    public GoodsServiceDto createGoods(String goodsName, Long price, Long quantity) {
        if (goodsRepository.findByGoodsName(goodsName).isPresent()) {
            throw new ExistGoodsException("이미 등록된 상품입니다.");
        }
        GoodsEntity goodsEntity = new GoodsEntity(goodsName, price);
        Long goodsId = goodsRepository.save(goodsEntity).getGoodsId();

        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, quantity);
        goodsStockRepository.save(goodsStockEntity);
        return new GoodsServiceDto(
                goodsId,
                goodsName,
                price,
                quantity
        );
    }

    public List<GoodsServiceDto> getAllGoods() {
        List<GoodsEntity> allGoodsList = goodsRepository.findAll();

        List<GoodsServiceDto> goodsServiceDtoList = new ArrayList<>();
        for (GoodsEntity goods : allGoodsList) {
            Optional<GoodsStockEntity> goodsStockEntity = goodsStockRepository.findByGoodsId(goods.getGoodsId());

            Long quantity = goodsStockEntity.map(GoodsStockEntity::getQuantity).orElse(0L);
            GoodsServiceDto response = new GoodsServiceDto(
                    goods.getGoodsId(),
                    goods.getGoodsName(),
                    goods.getPrice(),
                    quantity
            );
            goodsServiceDtoList.add(response);
        }
        return goodsServiceDtoList;
    }

    public GoodsServiceDto getOneGoodsInfo(Long goodsId) {
        if (goodsRepository.findByGoodsId(goodsId).isEmpty()){
            throw new InvalidGoodsException("상품 정보를 찾을 수 없습니다.");
        }
        Optional<GoodsEntity> goodsEntity = goodsRepository.findByGoodsId(goodsId);
        Long quantity = goodsStockRepository.findByGoodsId(goodsId).get().getQuantity();
        return new GoodsServiceDto(
                goodsId,
                goodsEntity.get().getGoodsName(),
                goodsEntity.get().getPrice(),
                quantity
        );
    }

    public List<SalesHistoryServiceDto> getBest10Goods() {
        LocalDateTime endDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime startDate = endDate.minusDays(3);

        Pageable topTen = PageRequest.of(0, 10);

        List<SalesHistoryEntity> result = salesHistoryRepository.findTop10GoodsSales(
                startDate, endDate, topTen
        );

        List<SalesHistoryServiceDto> salesHistoryServiceDtoList = result.stream().map(salesHistoryEntity -> new SalesHistoryServiceDto(
                salesHistoryEntity.getSalesHistoryId(), salesHistoryEntity.getGoodsId(), salesHistoryEntity.getUserId(), salesHistoryEntity.getQuantity()
        )).collect(Collectors.toList());

        return salesHistoryServiceDtoList;
    }
}
