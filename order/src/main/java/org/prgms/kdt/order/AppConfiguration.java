package org.prgms.kdt.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {"org.prgms.kdt.order.order", "org.prgms.kdt.order.voucher", "org.prgms.kdt.order.configuration"})
@PropertySource("application.properties")
public class AppConfiguration {

}
