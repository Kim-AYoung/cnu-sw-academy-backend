package org.prgms.kdt.springbootjpalecture.domain.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.prgms.kdt.springbootjpalecture.domain.parent.Parent;
import org.prgms.kdt.springbootjpalecture.domain.parent.ParentId;
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
public class ImproveMappingTest {

    @Autowired
    EntityManagerFactory emf;

    @Test
    void inheritance_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Food food = new Food();
        food.setPrice(1000);
        food.setStockQuantity(100);
        food.setChef("백종원");

        entityManager.persist(food);

        transaction.commit();
    }

    @Test
    void mapped_super_class_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();

        Order order = new Order();
        order.setUuid(UUID.randomUUID().toString());
        order.setMemo("부재시 전화주세요.");
        order.setOrderDatetime(LocalDateTime.now());
        order.setOrderStatus(OPENED);

        order.setCreatedBy("kimayoung");
        order.setCratedAt(LocalDateTime.now());

        entityManager.persist(order);

        transaction.commit();
    }

    @Test
    void multi_key_test() {
        EntityManager entityManager = emf.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        transaction.begin();

        Parent parent = new Parent();
        parent.setId(new ParentId("id1", "id2"));
        entityManager.persist(parent);

        transaction.commit();

        entityManager.clear();

        Parent parentEntity = entityManager.find(Parent.class, new ParentId("id1", "id2"));
        log.info("parent id1 : {}, id2 : {}", parentEntity.getId().getId1(), parentEntity.getId().getId2());
    }
}
