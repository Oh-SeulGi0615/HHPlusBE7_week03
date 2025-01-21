package kr.hhplus.be.server.domain.goods;

import kr.hhplus.be.server.api.request.GoodsRequest;
import kr.hhplus.be.server.api.response.GoodsResponse;
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

    public GoodsDomainDto createGoods(String goodsName, Long price, Long quantity) {
        if (goodsRepository.findByGoodsName(goodsName).isPresent()) {
            throw new ExistGoodsException("이미 등록된 상품입니다.");
        }
        GoodsEntity goodsEntity = new GoodsEntity(goodsName, price);
        Long goodsId = goodsRepository.save(goodsEntity).getGoodsId();

        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, quantity);
        goodsStockRepository.save(goodsStockEntity);
        return new GoodsDomainDto(
                goodsId,
                goodsName,
                price,
                quantity
        );
    }

    public List<GoodsDomainDto> getAllGoods() {
        List<GoodsEntity> allGoodsList = goodsRepository.findAll();

        List<GoodsDomainDto> goodsDomainDtoList = new ArrayList<>();
        for (GoodsEntity goods : allGoodsList) {
            Optional<GoodsStockEntity> goodsStockEntity = goodsStockRepository.findByGoodsId(goods.getGoodsId());

            Long quantity = goodsStockEntity.map(GoodsStockEntity::getQuantity).orElse(0L);
            GoodsDomainDto response = new GoodsDomainDto(
                    goods.getGoodsId(),
                    goods.getGoodsName(),
                    goods.getPrice(),
                    quantity
            );
            goodsDomainDtoList.add(response);
        }
        return goodsDomainDtoList;
    }

    public GoodsDomainDto getOneGoodsInfo(Long goodsId) {
        if (goodsRepository.findByGoodsId(goodsId).isEmpty()){
            throw new InvalidGoodsException("상품 정보를 찾을 수 없습니다.");
        }
        Optional<GoodsEntity> goodsEntity = goodsRepository.findByGoodsId(goodsId);
        Long quantity = goodsStockRepository.findByGoodsId(goodsId).get().getQuantity();
        return new  GoodsDomainDto(
                goodsId,
                goodsEntity.get().getGoodsName(),
                goodsEntity.get().getPrice(),
                quantity
        );
    }

    public List<SalesHistoryDomainDto> getBest10Goods() {
        LocalDateTime endDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime startDate = endDate.minusDays(3);

        Pageable topTen = PageRequest.of(0, 10);

        List<SalesHistoryEntity> result = salesHistoryRepository.findTop10GoodsSales(
                startDate, endDate, topTen
        );

        List<SalesHistoryDomainDto> salesHistoryDomainDtoList = result.stream().map(salesHistoryEntity -> new SalesHistoryDomainDto(
                salesHistoryEntity.getSalesHistoryId(), salesHistoryEntity.getGoodsId(), salesHistoryEntity.getUserId(), salesHistoryEntity.getQuantity()
        )).collect(Collectors.toList());

        return salesHistoryDomainDtoList;
    }
}
