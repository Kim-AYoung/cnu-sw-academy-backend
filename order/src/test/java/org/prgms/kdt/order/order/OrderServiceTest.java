package org.prgms.kdt.order.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prgms.kdt.order.voucher.FixedAmountVoucher;
import org.prgms.kdt.order.voucher.MemoryVoucherRepository;
import org.prgms.kdt.order.voucher.VoucherService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    class OrderRepositoryStub implements OrderRepository { // OrderRepository 구현체가 없을 경우 stub 생성

        @Override
        public Order insert(Order order) {
            return null;
        }
    }

    @Test
    @DisplayName("Order가 생성되어야 한다. (stub)")
    void createOrderByStub() {
        // Given
        var voucherRepository = new MemoryVoucherRepository();
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        voucherRepository.insert(fixedAmountVoucher);
        var sut = new OrderService(new OrderRepositoryStub(), new VoucherService(voucherRepository));

        // When
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)), fixedAmountVoucher.getVoucherId());

        // Then
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(), is(false));
        assertThat(order.getVoucher().get().getVoucherId(), is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(), is(OrderStatus.ACCEPTED));
    }

    @Test
    @DisplayName("Order가 생성되어야 한다. (mock)")
    void createOrderByMock() {
        // Given
        var voucherServiceMock = mock(VoucherService.class);
        var orderRepositoryMock = mock(OrderRepository.class);
        var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
        when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher);
        var sut = new OrderService(orderRepositoryMock, voucherServiceMock);

        // When
        var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(), 200, 1)), fixedAmountVoucher.getVoucherId());

        // Then
        var inOrder = inOrder(voucherServiceMock, orderRepositoryMock);
        inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId());
        inOrder.verify(orderRepositoryMock).insert(order);
        inOrder.verify(voucherServiceMock).useVoucher(fixedAmountVoucher);
    }
}