package com.prgrms.devcourse.springsecuritymasterclass.configure;

import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DataSourcePostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSource && !(bean instanceof Log4jdbcProxyDataSource)) {
            return new Log4jdbcProxyDataSource((DataSource) bean);
        } else {
            return bean;
        }
    }
}