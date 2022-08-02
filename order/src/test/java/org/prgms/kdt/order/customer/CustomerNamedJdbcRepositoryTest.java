package org.prgms.kdt.order.customer;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.Charset.UTF8;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_latest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerNamedJdbcRepositoryTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerNamedJdbcRepositoryTest.class);

    @Configuration
    @ComponentScan(basePackages = {"org.prgms.kdt.order.customer"})
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
    }

    @Autowired
    DataSource dataSource;

    EmbeddedMysql embeddedMysql;

    @Autowired
    CustomerNamedJdbcRepository customerJdbcRepository;

    Customer newCustomer;

    @BeforeAll
    void setUp() {
        newCustomer = new Customer(UUID.randomUUID(), "new-user", "new-user@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));

        var config = aMysqldConfig(v5_7_latest)
                .withCharset(UTF8)
                .withPort(2215)
                .withUser("test", "test1234!")
                .withTimeZone("Asia/Seoul")
                .build();

        embeddedMysql = anEmbeddedMysql(config)
                .addSchema("test-order_mgmt", ScriptResolver.classPathScript("schema.sql"))
                .start();
    }

    @AfterAll
    void cleanUp() {
        embeddedMysql.stop();
    }

    @Test
    @Order(1)
    public void testHikariConnectionPool() {
        assertThat(dataSource.getClass().getName()).isEqualTo("com.zaxxer.hikari.HikariDataSource");
    }

    @Test
    @Order(2)
    @DisplayName("고객 추가")
    void testInsert() {
        customerJdbcRepository.insert(newCustomer);

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get()).usingRecursiveComparison().isEqualTo(newCustomer);
    }

    @Test
    @Order(3)
    @DisplayName("전체 고객 조회")
    void testFindAll() {
        assertThat(customerJdbcRepository.findAll()).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객 조회")
    void testFindByName() {
        var customer = customerJdbcRepository.findByName(newCustomer.getName());
        assertThat(customer).isPresent();

        var unknown = customerJdbcRepository.findByName("unknown-user");
        assertThat(unknown).isNotPresent();
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 고객 조회")
    void testFindByEmail() {
        var customer = customerJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customer).isPresent();

        var unknown = customerJdbcRepository.findByEmail("unknown-user@gmail.com");
        assertThat(unknown).isNotPresent();
    }

    @Test
    @Order(6)
    @DisplayName("고객 수정")
    void testUpdate() {
        newCustomer.changeName("updated-user");
        customerJdbcRepository.update(newCustomer);

        var allCustomers = customerJdbcRepository.findAll();
        assertThat(allCustomers).hasSize(1);
        assertThat(allCustomers.stream().findFirst().get()).usingRecursiveComparison().isEqualTo(newCustomer);

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get()).usingRecursiveComparison().isEqualTo(newCustomer);
    }

    @Test
    @Order(7)
    @DisplayName("트렌젝션 테스트")
    void testTransaction() {
        var prevOne = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(prevOne).isPresent();

        var newOne = new Customer(UUID.randomUUID(), "a", "a@gmail.com", LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS));
        var insertedNewOne = customerJdbcRepository.insert(newOne);

        try {
            customerJdbcRepository.testTransaction(
                    new Customer(insertedNewOne.getCustomerId(),
                            "b",
                            prevOne.get().getEmail(),
                            newOne.getCreatedAt()));
        } catch (DataAccessException e) {
            logger.error("Got error when testing transaction", e);
        }

        var maybeNewOne = customerJdbcRepository.findById(insertedNewOne.getCustomerId());
        assertThat(maybeNewOne).isPresent();
        assertThat(maybeNewOne.get()).usingRecursiveComparison().isEqualTo(newOne);
    }
}