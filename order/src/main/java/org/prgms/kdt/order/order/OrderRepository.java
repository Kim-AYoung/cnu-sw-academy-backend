package org.prgms.kdt.order.order;

import org.prgms.kdt.order.order.Order;

public interface OrderRepository {
    Order insert(Order order);
}
