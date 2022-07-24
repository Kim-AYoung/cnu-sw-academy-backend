package org.prgms.kdt.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order.order", "org.prgms.kdt.order.voucher", "org.prgms.kdt.order.configuration"})
public class AppConfiguration {
    @Bean(initMethod = "init")
    public BeanOne beanOne() {
        return new BeanOne();
    }
}

class BeanOne{
    public void init() {
        System.out.println("[BeanOne] init called");
    }
}
