package org.prgms.kdt.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.kdt.order.order.OrderItem;
import org.prgms.kdt.order.order.OrderService;
import org.prgms.kdt.order.order.OrderStatus;
import org.prgms.kdt.order.voucher.FixedAmountVoucher;
import org.prgms.kdt.order.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringJUnitConfig
@ActiveProfiles("test")
public class OrderSpringContextTests {

    @Configuration
    @ComponentScan(basePackages = {"org.prgms.kdt.order.order", "org.prgms.kdt.order.voucher"})
    static class Config {

    }

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    OrderService orderService;

    @Autowired
    VoucherRepository voucherRepository;

    @Test
    @DisplayName("ApplicationContext가 생성되어야함")
    public void testApplicationContext() {
        assertThat(applicationContext , notNullValue());
    }

    @Test
    @DisplayName("VoucherRepository가 Bean으로 등록되어있어야함")
    public void testVoucherRepositoryCreation() {
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        assertThat(voucherRepository, notNullValue());
    }

    @Test
    @DisplayName("주문을 생성할 수 있음")
    public void testCreateOrder() {
        // Given
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);

        // When
        var order = orderService.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)), fixedAmountVoucher.getVoucherId());

        // Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }
}
