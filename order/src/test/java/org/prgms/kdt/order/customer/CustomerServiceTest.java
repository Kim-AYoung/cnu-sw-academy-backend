package org.prgms.kdt.order.customer;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.prgms.kdt.order.customer.model.Customer;
import org.prgms.kdt.order.customer.repository.CustomerNamedJdbcRepository;
import org.prgms.kdt.order.customer.repository.CustomerRepository;
import org.prgms.kdt.order.customer.service.CustomerService;
import org.prgms.kdt.order.customer.service.CustomerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringJUnitConfig
class CustomerServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Configuration
    @EnableTransactionManagement
    static class Config {

        @Bean
        public DataSource dataSource() {
            var dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost:2215/test-order_mgmt")
                    .username("test")
                    .password("test1234!")
                    .type(HikariDataSource.class)
                    .build();
//            dataSource.setMinimumIdle(100);
//            dataSource.setMaximumPoolSize(1000);
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        @Bean
        public NamedParameterJdbcTemplate namedParameterJdbcTemplate(JdbcTemplate jdbcTemplate) {
            return new NamedParameterJdbcTemplate(jdbcTemplate);
        }

        @Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
        @Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager) {
            return new TransactionTemplate(platformTransactionManager);
        }

        @Bean
        public CustomerRepository customerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
            return new CustomerNamedJdbcRepository(namedParameterJdbcTemplate);
        }

        @Bean
        public CustomerService customerService(CustomerRepository customerRepository) {
            return new CustomerServiceImpl(customerRepository);
        }
    }

    static EmbeddedMysql embeddedMysql;

    @BeforeAll
    static void setUp() {
        var mysqlConfig = aMysqldConfig(v5_7_latest)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();

        embeddedMysql = anEmbeddedMysql(mysqlConfig)
                .addSchema("test-order_mgmt", ScriptResolver.classPathScript("schema.sql"))
                .start();
    }

    @AfterAll
    static void cleanUp() {
        embeddedMysql.stop();
    }

    @AfterEach
    void dataCleanUp() {
        customerRepository.deleteAll();
    }

    @Autowired
    CustomerService customerService;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    @DisplayName("다건 추가 테스트")
    void testCreateCustomers() {
        var customers = List.of(
            new Customer(UUID.randomUUID(), "a", "a@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
            new Customer(UUID.randomUUID(), "b", "b@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        );

        customerService.createCustomers(customers);
        var allCustomersRetrieved = customerRepository.findAll();
        assertThat(allCustomersRetrieved.size(), is(2));
        assertThat(allCustomersRetrieved, containsInAnyOrder(samePropertyValuesAs(customers.get(0)), samePropertyValuesAs(customers.get(1))));
    }

    @Test
    @DisplayName("다건 추가 실패시 전체 트렌젝션이 롤백되어야함")
    void testCreateCustomersRollback() {
        var customers = List.of(
                new Customer(UUID.randomUUID(), "c", "c@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)),
                new Customer(UUID.randomUUID(), "d", "c@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS))
        );
        try {
            customerService.createCustomers(customers);
        } catch(DataAccessException e) {
            logger.error("Got error when create customers", e);
        }
        var allCustomersRetrieved = customerRepository.findAll();
        assertThat(allCustomersRetrieved.isEmpty(), is(true));
    }
}