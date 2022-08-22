package org.prgms.kdt.springbootjpalecture.order.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prgms.kdt.springbootjpalecture.domain.order.Member;
import org.prgms.kdt.springbootjpalecture.domain.order.OrderRepository;
import org.prgms.kdt.springbootjpalecture.domain.order.OrderStatus;
import org.prgms.kdt.springbootjpalecture.item.dto.ItemDto;
import org.prgms.kdt.springbootjpalecture.item.dto.ItemType;
import org.prgms.kdt.springbootjpalecture.member.dto.MemberDto;
import org.prgms.kdt.springbootjpalecture.order.dto.OrderDto;
import org.prgms.kdt.springbootjpalecture.order.dto.OrderItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    String uuid = UUID.randomUUID().toString();

    @BeforeEach
    void save_test() {
        // Given
        OrderDto orderDto = OrderDto.builder()
                .uuid(uuid)
                .memo("문앞 보관 해주세요.")
                .orderDatetime(LocalDateTime.now())
                .orderStatus(OrderStatus.OPENED)
                .memberDto(
                        MemberDto.builder()
                                .name("kimayoung")
                                .nickName("guppy.kang")
                                .address("서울시 동작구")
                                .age(24)
                                .description("백엔드 개발자입니다.")
                                .build()
                )
                .orderItemDtos(List.of(
                        OrderItemDto.builder()
                                .price(1000)
                                .quantity(100)
                                .itemDtos(List.of(
                                        ItemDto.builder()
                                                .type(ItemType.FOOD)
                                                .chef("백종원")
                                                .price(1000)
                                                .build()
                                ))
                                .build()
                ))
                .build();
        // When
        String savedUuid = orderService.save(orderDto);

        // Then
        // assertThat(uuid).isEqualTo(savedUuid);
        log.info("saved order uuid : {}", savedUuid);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void findAll() {
        // Given
        PageRequest page = PageRequest.of(0, 10);

        // When
        Page<OrderDto> orders = orderService.findOrders(page);

        // Then
        assertThat(orders.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findOne() throws Exception {
        // When
        OrderDto order = orderService.findOne(uuid);

        // Then
        assertThat(order.getUuid()).isEqualTo(uuid);
    }
}