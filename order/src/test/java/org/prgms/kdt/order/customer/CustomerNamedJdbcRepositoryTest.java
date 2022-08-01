package org.prgms.kdt.order.customer;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.ScriptResolver;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

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
    }

    @Autowired
    DataSource dataSource;

    EmbeddedMysql embeddedMysql;

    @Autowired
    CustomerNamedJdbcRepository customerNamedJdbcRepository;

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
        customerNamedJdbcRepository.insert(newCustomer);

        var retrievedCustomer = customerNamedJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get()).usingRecursiveComparison().isEqualTo(newCustomer);
    }

    @Test
    @Order(3)
    @DisplayName("전체 고객 조회")
    void testFindAll() {
        assertThat(customerNamedJdbcRepository.findAll()).isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("이름으로 고객 조회")
    void testFindByName() {
        var customer = customerNamedJdbcRepository.findByName(newCustomer.getName());
        assertThat(customer).isPresent();

        var unknown = customerNamedJdbcRepository.findByName("unknown-user");
        assertThat(unknown).isNotPresent();
    }

    @Test
    @Order(5)
    @DisplayName("이메일로 고객 조회")
    void testFindByEmail() {
        var customer = customerNamedJdbcRepository.findByEmail(newCustomer.getEmail());
        assertThat(customer).isPresent();

        var unknown = customerNamedJdbcRepository.findByEmail("unknown-user@gmail.com");
        assertThat(unknown).isNotPresent();
    }

    @Test
    @Order(6)
    @DisplayName("고객 수정")
    void testUpdate() {
        newCustomer.changeName("updated-user");
        customerNamedJdbcRepository.update(newCustomer);

        var allCustomers = customerNamedJdbcRepository.findAll();
        assertThat(allCustomers).hasSize(1);
        assertThat(allCustomers.stream().findFirst().get()).usingRecursiveComparison().isEqualTo(newCustomer);

        var retrievedCustomer = customerNamedJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get()).usingRecursiveComparison().isEqualTo(newCustomer);
    }
}