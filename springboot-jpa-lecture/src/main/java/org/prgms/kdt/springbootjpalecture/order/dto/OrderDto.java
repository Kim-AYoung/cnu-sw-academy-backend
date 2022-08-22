package org.prgms.kdt.springbootjpalecture.order.dto;

import lombok.*;
import org.prgms.kdt.springbootjpalecture.domain.order.OrderStatus;
import org.prgms.kdt.springbootjpalecture.member.dto.MemberDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private String uuid;
    private LocalDateTime orderDatetime;
    private OrderStatus orderStatus;
    private String memo;

    private MemberDto memberDto;
    private List<OrderItemDto> orderItemDtos;

}
