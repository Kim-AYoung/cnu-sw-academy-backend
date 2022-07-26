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
@ComponentScan(basePackages = {"org.prgms.kdt.order.order", "org.prgms.kdt.order.voucher", "org.prgms.kdt.order.configuration"})
public class OrderApplication {

    public static void main(String[] args) {
        var springApplication = new SpringApplication(OrderApplication.class);
        // springApplication.setAdditionalProfiles("dev");
        var applicationContext = springApplication.run(args);

        var orderService = applicationContext.getBean(OrderService.class);

        var customerId = UUID.randomUUID();
        var orderItems = new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }};
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JDBCVoucherRepository));
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        var order = orderService.createOrder(customerId, orderItems, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));
    }
}
