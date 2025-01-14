package kr.hhplus.be.server.domain.order;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.goods.GoodsRepository;
import kr.hhplus.be.server.domain.goods.GoodsStockRepository;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.exeption.customExceptions.GoodsOutOfStockException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidGoodsException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidOrderException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidUserException;
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

        ArrayList<OrderResponse> orderResponseList = new ArrayList<>();
        for (OrderRequest orderRequests:orderRequestList){
            if (goodsRepository.findByGoodsId(orderRequests.getGoodsId()).isEmpty()){
                throw new InvalidGoodsException("상품정보를 찾을 수 없습니다.");
            }
            if (goodsStockRepository.findByGoodsId(orderRequests.getGoodsId()).get().getQuantity() < orderRequests.getQuantity()){
                throw new GoodsOutOfStockException("상품 재고가 부족합니다.");
            }

            orderDetailRepository.save(new OrderDetailEntity(orderId, orderRequests.getGoodsId(), orderRequests.getQuantity()));
            OrderResponse orderResponse = new OrderResponse(orderId, userId, orderRequests.getGoodsId(), orderRequests.getQuantity());
            orderResponseList.add(orderResponse);
        }
        return orderResponseList;
    }

    public List<OrderEntity> getMyAllOrder(Long userId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));

        List<OrderEntity> allMyOrderList = orderRepository.findAllByUserId(userId);
        if (allMyOrderList.isEmpty()) {
            throw new InvalidOrderException("주문 정보를 찾을 수 없습니다.");
        }

        return allMyOrderList;
    }

    public List<OrderResponse> getMyDetailOrder(Long userId, Long orderId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvalidOrderException("주문 정보를 찾을 수 없습니다."));

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

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new InvalidUserException("유저를 찾을 수 없습니다."));
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new InvalidOrderException("주문 정보를 찾을 수 없습니다."));

        orderEntity.setStatus(OrderStatus.CANCELED);
        return new OrderResponse(orderId, userId, orderRepository.findByOrderId(orderId).get().getStatus());
    }
}
