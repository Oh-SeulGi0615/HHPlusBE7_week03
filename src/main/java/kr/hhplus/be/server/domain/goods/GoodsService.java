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

    public GoodsResponse createGoods(GoodsRequest goodsRequest) {
        if (goodsRepository.findByGoodsName(goodsRequest.getGoodsName()).isPresent()) {
            throw new ExistGoodsException("이미 등록된 상품입니다.");
        }
        GoodsEntity goodsEntity = new GoodsEntity(goodsRequest.getGoodsName(), goodsRequest.getPrice());
        Long goodsId = goodsRepository.save(goodsEntity).getGoodsId();

        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, goodsRequest.getQuantity());
        goodsStockRepository.save(goodsStockEntity);
        return new GoodsResponse(
                goodsId,
                goodsRequest.getGoodsName(),
                goodsRequest.getPrice(),
                goodsRequest.getQuantity()
        );
    }

    public List<GoodsResponse> getAllGoods() {
        List<GoodsEntity> allGoodsList = goodsRepository.findAll();

        List<GoodsResponse> goodsResponseList = new ArrayList<>();
        for (GoodsEntity goods : allGoodsList) {
            Optional<GoodsStockEntity> goodsStockEntity = goodsStockRepository.findByGoodsId(goods.getGoodsId());

            Long quantity = goodsStockEntity.map(GoodsStockEntity::getQuantity).orElse(0L);
            GoodsResponse response = new GoodsResponse(
                    goods.getGoodsId(),
                    goods.getGoodsName(),
                    goods.getPrice(),
                    quantity
            );
            goodsResponseList.add(response);
        }
        return goodsResponseList;
    }

    public GoodsResponse getOneGoodsInfo(Long goodsId) {
        if (goodsRepository.findByGoodsId(goodsId).isEmpty()){
            throw new InvalidGoodsException("상품 정보를 찾을 수 없습니다.");
        }
        Optional<GoodsEntity> goodsEntity = goodsRepository.findByGoodsId(goodsId);
        Long quantity = goodsStockRepository.findByGoodsId(goodsId).get().getQuantity();
        return new  GoodsResponse(
                goodsId,
                goodsEntity.get().getGoodsName(),
                goodsEntity.get().getPrice(),
                quantity
        );
    }

    public List<SalesHistoryEntity> getBest10Goods() {
        LocalDateTime endDate = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime startDate = endDate.minusDays(3);

        Pageable topTen = PageRequest.of(0, 10);

        List<SalesHistoryEntity> result = salesHistoryRepository.findTop10GoodsSales(
                startDate, endDate, topTen
        );

        return result;
    }
}
