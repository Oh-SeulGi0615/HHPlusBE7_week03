package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.goods.GoodsEntity;
import kr.hhplus.be.server.domain.goods.GoodsRepository;
import kr.hhplus.be.server.domain.goods.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.GoodsStockRepository;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.exeption.GoodsOutOfStockException;
import kr.hhplus.be.server.exeption.InvalidGoodsException;
import kr.hhplus.be.server.exeption.InvalidOrderException;
import kr.hhplus.be.server.exeption.InvalidUserException;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문생성_성공케이스() {
        // given
        Long userId = 1L;
        Long goodsId = 100L;
        Long quantity = 2L;

        List<OrderRequest> orderRequests = List.of(new OrderRequest(goodsId, quantity));
        GoodsEntity goodsEntity = new GoodsEntity("Test Goods", 1000L);
        goodsEntity.setGoodsId(goodsId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new UserEntity("test")));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(new OrderEntity(userId));
        when(goodsRepository.findByGoodsId(goodsId)).thenReturn(Optional.of(goodsEntity));

        // when
        List<OrderResponse> responses = orderService.createOrder(userId, orderRequests);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(goodsId, responses.get(0).getGoodsId());
        assertEquals(quantity, responses.get(0).getQuantity());
    }

    @Test
    void 주문생성_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        List<OrderRequest> orderRequestList = Arrays.asList(new OrderRequest(1L, 2L));
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(InvalidUserException.class, () -> orderService.createOrder(userId, orderRequestList));

        // then
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 주문생성_없는상품_실패케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        Long goodsId = 999L;
        Long quantity = 2L;

        List<OrderRequest> orderRequests = List.of(new OrderRequest(goodsId, quantity));
        OrderEntity orderEntity = new OrderEntity(userId);
        orderEntity.setOrderId(orderId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new UserEntity("test")));
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(goodsRepository.findByGoodsId(goodsId)).thenReturn(Optional.empty());

        // when & then
        InvalidGoodsException exception = assertThrows(
                InvalidGoodsException.class,
                () -> orderService.createOrder(userId, orderRequests)
        );

        assertEquals("상품정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 주문조회_성공케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        UserEntity userEntity = new UserEntity("test");
        OrderEntity orderEntity = new OrderEntity(userId);
        orderEntity.setOrderId(orderId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(orderRepository.findByUserId(userId)).thenReturn(Optional.of(orderEntity));
        when(orderDetailRepository.findAllByOrderId(orderId)).thenReturn(List.of(new OrderDetailEntity(orderId, 1L, 2L)));

        // when
        List<OrderResponse> responses = orderService.getMyOrder(userId);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getGoodsId());
    }

    @Test
    void 주문조회_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(InvalidUserException.class, () -> orderService.getMyOrder(userId));

        // then
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 주문취소_성공케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new UserEntity("test")));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(new OrderEntity(userId)));
        when(orderRepository.updateOrderStatus(orderId, OrderStatus.CANCELED)).thenReturn(new OrderEntity(userId));

        // when
        OrderResponse response = orderService.cancelOrder(userId, orderId);

        // then
        assertNotNull(response);
        assertEquals(OrderStatus.CANCELED, response.getStatus());
    }

    @Test
    void 주문취소_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(InvalidUserException.class, () -> orderService.cancelOrder(userId, orderId));

        // then
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 주문취소_없는주문_실패케이스() {
        // given
        Long userId = 1L;
        Long orderId = 1L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new UserEntity("test")));
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(InvalidOrderException.class, () -> orderService.cancelOrder(userId, orderId));

        // then
        assertEquals("주문 정보를 찾을 수 없습니다.", exception.getMessage());
    }
}