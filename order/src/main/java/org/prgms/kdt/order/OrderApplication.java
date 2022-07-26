package org.prgms.kdt.order;

import org.prgms.kdt.order.configuration.YamlPropertiesFactory;
import org.prgms.kdt.order.order.OrderItem;
import org.prgms.kdt.order.order.OrderProperties;
import org.prgms.kdt.order.order.OrderService;
import org.prgms.kdt.order.voucher.FixedAmountVoucher;
import org.prgms.kdt.order.voucher.JDBCVoucherRepository;
import org.prgms.kdt.order.voucher.VoucherRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.UUID;

@SpringBootApplication
@ComponentScan(basePackages = {"org.prgms.kdt.order.order", "org.prgms.kdt.order.voucher"})
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);

    }
}
