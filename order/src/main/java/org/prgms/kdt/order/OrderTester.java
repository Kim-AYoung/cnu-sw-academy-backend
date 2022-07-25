package org.prgms.kdt.order;

import org.prgms.kdt.order.order.OrderItem;
import org.prgms.kdt.order.order.OrderProperties;
import org.prgms.kdt.order.order.OrderService;
import org.prgms.kdt.order.voucher.FixedAmountVoucher;
import org.prgms.kdt.order.voucher.JDBCVoucherRepository;
import org.prgms.kdt.order.voucher.VoucherRepository;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderTester {
    public static void main(String[] args) {

        var applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh();

        var orderService = applicationContext.getBean(OrderService.class);

        var customerId = UUID.randomUUID();
        var orderItems = new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }};
        var voucherRepository = applicationContext.getBean(VoucherRepository.class);
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JDBCVoucherRepository));
        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        var order = orderService.createOrder(customerId, orderItems, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        // environment 불러오기
//        var environment = applicationContext.getEnvironment();
//        var version = environment.getProperty("kdt.version");
//        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount", Integer.class);
//        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
//        var description = environment.getProperty("kdt.description", List.class);
//        var orderProperties = applicationContext.getBean(OrderProperties.class);
//        System.out.println(MessageFormat.format("version -> {0}", orderProperties.getVersion()));
//        System.out.println(MessageFormat.format("minimumOrderAmount -> {0}", orderProperties.getMinimumOrderAmount()));
//        System.out.println(MessageFormat.format("supportVendors -> {0}", orderProperties.getSupportVendors()));
//        System.out.println(MessageFormat.format("description -> {0}", orderProperties.getDescription()));

        applicationContext.close();
    }
}
