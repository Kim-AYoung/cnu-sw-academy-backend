package org.prgms.kdt.springbootjpalecture.domain.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private int price;
    private int quantity;

    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    public void setOrder(Order order) {
        if (Objects.nonNull(this.order)) {
            this.order.getOrderItems().remove(this);
        }

        this.order = order;
        order.getOrderItems().add(this);
    }

    public void setItem(Item item) {
        if (Objects.nonNull(this.item)) {
            this.item.getOrderItems().remove(this);
        }

        this.item = item;
        item.getOrderItems().add(this);
    }
}
