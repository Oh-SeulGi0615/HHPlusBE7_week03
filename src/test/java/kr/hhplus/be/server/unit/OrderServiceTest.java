package kr.hhplus.be.server.unit;

import kr.hhplus.be.server.api.request.OrderRequest;
import kr.hhplus.be.server.api.response.OrderResponse;
import kr.hhplus.be.server.domain.goods.GoodsEntity;
import kr.hhplus.be.server.domain.goods.GoodsRepository;
import kr.hhplus.be.server.domain.goods.GoodsStockEntity;
import kr.hhplus.be.server.domain.goods.GoodsStockRepository;
import kr.hhplus.be.server.domain.order.*;
import kr.hhplus.be.server.domain.user.UserEntity;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.enums.OrderStatus;
import kr.hhplus.be.server.exeption.customExceptions.InvalidGoodsException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidOrderException;
import kr.hhplus.be.server.exeption.customExceptions.InvalidUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private GoodsRepository goodsRepository;

    @Mock
    private GoodsStockRepository goodsStockRepository;

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
        Long goodsId = 1L;
        Long quantity = 2L;

        List<OrderRequest> orderRequests = List.of(new OrderRequest(goodsId, quantity));
        GoodsEntity goodsEntity = new GoodsEntity("Test Goods", 1000L);
        GoodsStockEntity goodsStockEntity = new GoodsStockEntity(goodsId, 100L);
        goodsEntity.setGoodsId(goodsId);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(new UserEntity("test")));
        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> {
                    OrderEntity savedOrder = invocation.getArgument(0);
                    savedOrder.setOrderId(123L);
                    return savedOrder;
                });
        when(goodsRepository.findByGoodsId(goodsId))
                .thenReturn(Optional.of(goodsEntity));
        when(goodsStockRepository.findByGoodsId(goodsId))
                .thenReturn(Optional.of(goodsStockEntity));

        // when
        List<OrderDomainDto> responses = orderService.createOrder(userId, orderRequests);

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
        Long orderId1 = 1L;
        Long orderId2 = 2L;
        UserEntity userEntity = new UserEntity("test");

        OrderEntity orderEntity1 = new OrderEntity(userId);
        orderEntity1.setOrderId(orderId1);
        OrderEntity orderEntity2 = new OrderEntity(userId);
        orderEntity2.setOrderId(orderId2);

        List<OrderEntity> orderList = new ArrayList<>();
        orderList.add(orderEntity1);
        orderList.add(orderEntity2);

        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(userEntity));
        when(orderRepository.findAllByUserId(userId)).thenReturn(orderList);

        // when
        List<MyOrderDomainDto> responses = orderService.getMyAllOrder(userId);

        // then
        assertNotNull(responses);
        assertEquals(2, responses.size());
    }

    @Test
    void 주문조회_없는유저_실패케이스() {
        // given
        Long userId = 1L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        Exception exception = assertThrows(InvalidUserException.class, () -> orderService.getMyAllOrder(userId));

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

        // when
        OrderDomainDto response = orderService.cancelOrder(userId, orderId);

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