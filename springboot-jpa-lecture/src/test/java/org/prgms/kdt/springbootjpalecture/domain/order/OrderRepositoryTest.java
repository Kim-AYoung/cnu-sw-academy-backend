package org.prgms.kdt.springbootjpalecture.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.prgms.kdt.springbootjpalecture.domain.order.OrderStatus.OPENED;

@Slf4j
@SpringBootTest
class OrderRepositoryTest {

    String uuid = UUID.randomUUID().toString();

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp () {
        Order order = new Order();
        order.setUuid(uuid);
        order.setOrderStatus(OPENED);
        order.setOrderDatetime(LocalDateTime.now());
        order.setMemo("부재시 전화주세요.");
        order.setCreatedBy("kimayoung");
        order.setCreatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
    }

    @Test
    void JPA_QUERY() {
        Order order = orderRepository.findById(uuid).get();
        List<Order> all = orderRepository.findAll();
    }

    @Test
    void METHOD_QUERY() {
        orderRepository.findAllByOrderStatus(OrderStatus.OPENED);
        orderRepository.findAllByOrderStatusOrderByOrderDatetime(OrderStatus.OPENED);
    }

    @Test
    void NAMED_QUERY() {
        Optional<Order> order = orderRepository.findByMemo("전화");

        Order entity = order.get();
        log.info("{}", entity.getMemo());
    }
}