package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.goods.GoodsRepository;
import kr.hhplus.be.server.domain.goods.GoodsStockRepository;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.exeption.GoodsOutOfStockException;
import kr.hhplus.be.server.exeption.InvalidGoodsException;
import kr.hhplus.be.server.exeption.InvalidUserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final GoodsRepository goodsRepository;
    private final GoodsStockRepository goodsStockRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Autowired
    public OrderService(UserRepository userRepository, GoodsRepository goodsRepository, GoodsStockRepository goodsStockRepository, OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.userRepository = userRepository;
        this.goodsRepository = goodsRepository;
        this.goodsStockRepository = goodsStockRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<OrderResponse> createOrder(Long userId, List<OrderRequest> orderRequestList) {
        if (userRepository.findByUserId(userId).isEmpty()){
            throw new InvalidUserException("유저를 찾을 수 없습니다.");
        }
        OrderEntity orderEntity = new OrderEntity(userId);
        Long orderId = orderRepository.save(orderEntity).getOrderId();

        List<OrderResponse> orderResponseList = new ArrayList<>();
        for (OrderRequest orderRequests:orderRequestList){
            if (goodsRepository.findByGoodsId(orderRequests.getGoodsId()).isEmpty()){
                throw new InvalidGoodsException("상품정보를 찾을 수 없습니다.");
            }
            if (goodsStockRepository.findByGoodsId(orderRequests.getGoodsId()).get().getQuantity() < orderRequests.getQuantity()){
                throw new GoodsOutOfStockException("상품의 재고가 부족합니다.");
            }
            OrderResponse orderResponse = new OrderResponse(orderId, userId, orderRequests.getGoodsId(), orderRequests.getQuantity());
            orderResponseList.add(orderResponse);
        }
        return orderResponseList;
    }

    public List<OrderResponse> getMyOrder(Long userId) {
        if (userRepository.findByUserId(userId).isEmpty()){
            throw new InvalidUserException("유저를 찾을 수 없습니다.");
        }
        Long orderId = orderRepository.findByUserId(userId).get().getOrderId();

        List<OrderResponse> myOrderList = new ArrayList<>();
        List<OrderDetailEntity> OrderList = orderDetailRepository.findAllByOrderId(orderId);
        for (OrderDetailEntity myOrder: OrderList){
            OrderResponse orderResponse = new OrderResponse(
                    orderId, userId, myOrder.getGoodsId(), myOrder.getQuantity()
            );
            myOrderList.add(orderResponse);
        }
        return myOrderList;
    }
}
