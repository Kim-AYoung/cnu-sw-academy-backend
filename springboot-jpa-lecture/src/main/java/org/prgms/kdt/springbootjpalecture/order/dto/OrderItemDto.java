package org.prgms.kdt.springbootjpalecture.order.dto;

import lombok.*;
import org.prgms.kdt.springbootjpalecture.item.dto.ItemDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Integer price;
    private Integer quantity;

    private List<ItemDto> itemDtos;
}
