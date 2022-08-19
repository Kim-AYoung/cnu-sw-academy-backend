package org.prgms.kdt.springbootjpalecture.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.prgms.kdt.springbootjpalecture.domain.order.OrderStatus.OPENED;

@Slf4j
@SpringBootTest
public class ProxyTest {

    @Autowired
    EntityManagerFactory emf;

    private String uuid = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        // 주문 엔티티
        Order order = new Order();
        order.setUuid(uuid);
        order.setMemo("부재시 전화주세요.");
        order.setOrderDatetime(LocalDateTime.now());
        order.setOrderStatus(OPENED);

        entityManager.persist(order);

        // 회원 엔티티
        Member member = new Member();
        member.setName("kimayoung");
        member.setAddress("서울시 동작구");
        member.setAge(24);
        member.setNickName("KIMA");
        member.setDescription("백엔드 개발자입니다.");
        member.addOrder(order); // 연관관계 편의 메소드 사용

        entityManager.persist(member);
        transaction.commit();
    }

    @Test
    void proxy() {
        EntityManager entityManager = emf.createEntityManager();
        Order order = entityManager.find(Order.class, uuid);

        Member member = order.getMember();
        log.info("MEMBER USE BEFORE IS-LOADED : {}", emf.getPersistenceUnitUtil().isLoaded(member)); // Eager -> member : Entity 객체, Lazy -> member : Proxy 객체
        String nickname = member.getNickName();
        log.info("MEMBER USE BEFORE IS-LOADED : {}", emf.getPersistenceUnitUtil().isLoaded(member)); // Eager, Lazy -> member : Entity 객체
    }

    @Test
    void move_persist() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        Order order = entityManager.find(Order.class, uuid); // 영속 상태

        transaction.begin();

        OrderItem orderItem = new OrderItem(); // 준영속 상태
        orderItem.setQuantity(10);
        orderItem.setPrice(1000);

        order.addOrderItem(orderItem); // OrderItem : 준영속 상태 -> 영속 상태 (영속성 전이)

        transaction.commit(); // flush
    }

    @Test
    void orphan_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        Order order = entityManager.find(Order.class, uuid);

        transaction.begin();

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setPrice(1000);

        order.addOrderItem(orderItem);

        transaction.commit();

        entityManager.clear();

        Order order2 = entityManager.find(Order.class, uuid);

        transaction.begin();
        order2.getOrderItems().remove(0); // 고아 상태
        transaction.commit();
    }
}
