package org.prgms.kdt.springbootjpalecture.order.service;

import lombok.RequiredArgsConstructor;
import org.prgms.kdt.springbootjpalecture.domain.order.Order;
import org.prgms.kdt.springbootjpalecture.domain.order.OrderRepository;
import org.prgms.kdt.springbootjpalecture.order.converter.OrderConverter;
import org.prgms.kdt.springbootjpalecture.order.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderConverter orderConverter;
    private final OrderRepository orderRepository;

    public String save(OrderDto orderDto) {
        Order order = orderConverter.convertOrder(orderDto);
        Order save = orderRepository.save(order);

        return save.getUuid();
    }

    public String update(String uuid, OrderDto orderDto) throws Exception {
        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new Exception("주문을 찾을 수 없습니다."));
        order.setMemo(orderDto.getMemo());
        order.setOrderStatus(order.getOrderStatus());

        return order.getUuid();
    }

    public OrderDto findOne(String uuid) throws Exception {
        return orderRepository.findById(uuid)
                .map(orderConverter::convertOrderDto)
                .orElseThrow(() -> new Exception("주문을 찾을 수 없습니다."));
    }

    public Page<OrderDto> findOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderConverter::convertOrderDto);
    }
}
