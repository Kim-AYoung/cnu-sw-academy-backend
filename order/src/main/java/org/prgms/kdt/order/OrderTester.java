package org.prgms.kdt.order;

import org.apache.commons.logging.LogFactory;
import org.prgms.kdt.order.order.OrderItem;
import org.prgms.kdt.order.order.OrderProperties;
import org.prgms.kdt.order.order.OrderService;
import org.prgms.kdt.order.voucher.FixedAmountVoucher;
import org.prgms.kdt.order.voucher.JDBCVoucherRepository;
import org.prgms.kdt.order.voucher.VoucherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class OrderTester {

    private static final Logger logger = LoggerFactory.getLogger(OrderTester.class);

    public static void main(String[] args) throws IOException {

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
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository instanceof JDBCVoucherRepository));
//        System.out.println(MessageFormat.format("is Jdbc Repo -> {0}", voucherRepository.getClass().getCanonicalName()));
        var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(), 10L));

        var order = orderService.createOrder(customerId, orderItems, voucher.getVoucherId());

        Assert.isTrue(order.totalAmount() == 90L, MessageFormat.format("totalAmount {0} is not 90L", order.totalAmount()));

        OrderProperties orderProperties = applicationContext.getBean(OrderProperties.class);
        logger.info("logger name -> {}", logger.getName());
        logger.info("version -> {}", orderProperties.getVersion());
        logger.info("minimumOrderAmount -> {}", orderProperties.getMinimumOrderAmount());
        logger.info("supportVendors -> {}", orderProperties.getSupportVendors());
        logger.info("description -> {}", orderProperties.getDescription());

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

        // resource 불러오기
//        var resource = applicationContext.getResource("classpath:application.yaml");
//        var resource2 = applicationContext.getResource("file:temp.txt");
//        System.out.println(MessageFormat.format("Resource -> {0}", resource.getClass().getCanonicalName()));
//        var file = resource.getFile();
//        var strings = Files.readAllLines(file.toPath());
//        System.out.println(strings.stream().collect(Collectors.joining("\n")));
//
//        var resource3 = applicationContext.getResource("https://jsonplaceholder.typicode.com/todos/1");
//        System.out.println(MessageFormat.format("Resource -> {0}", resource3.getClass().getCanonicalName()));
//        var readableByteChannel = Channels.newChannel(resource3.getURL().openStream());
//        var bufferedReader = new BufferedReader(Channels.newReader(readableByteChannel, StandardCharsets.UTF_8));
//        var contents = bufferedReader.lines().collect(Collectors.joining("\n"));
//        System.out.println(contents);

        applicationContext.close();
    }
}
