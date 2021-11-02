package zabolotnyi.springsecurity;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import zabolotnyi.springsecurity.security.CustomRole;
import zabolotnyi.springsecurity.security.CustomUser;
import zabolotnyi.springsecurity.security.RoleRepository;
import zabolotnyi.springsecurity.security.UserRepository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderResourceTest {

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @LocalServerPort
    Integer port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    @BeforeEach
    void setUp() {

        // setup
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                userRepository.deleteAllInBatch();
                roleRepository.deleteAllInBatch();
                orderRepository.deleteAllInBatch();
            }
        });
    }

    @Test
    void shouldCreateOrderWithBasicAuthWithoutAuthorization() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ROLE_USER")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));

        // and
        OrderView orderToSave = new OrderView("test", 14, 5);
        HttpEntity<OrderView> httpEntity = new HttpEntity<>(orderToSave);

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "login", "password").exchange(
                "http://localhost:" + port + "/api/v1/orders/post",
                HttpMethod.POST, httpEntity, OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // and
        OrderDetails body = response.getBody();
        assertThat(body.getName()).isEqualTo(orderToSave.getName());
        assertThat(body.getId()).isNotNull();
        assertThat(body.getTotal()).isEqualTo(orderToSave.getPrice() * orderToSave.getQuantity());

    }

    @Test
    void shouldNotCreateOrderWithWrongLogin() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ROLE_USER")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));

        // and
        OrderView orderToSave = new OrderView("test", 14, 5);
        HttpEntity<OrderView> httpEntity = new HttpEntity<>(orderToSave);

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "wronglogin", "password").exchange(
                "http://localhost:" + port + "/api/v1/orders/post",
                HttpMethod.POST, httpEntity, OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void shouldGetOrderWithBasicAuthWithAuthorization() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ADMIN")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));
        OrderDetails orderSaved = transactionTemplate.execute(status ->
                orderRepository.save(new OrderDetails(1L, "test", 14, 5, 70)));

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "login", "password").getForEntity(
                "http://localhost:" + port + "/api/v1/orders/get/" + orderSaved.getId(), OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // and
        OrderDetails body = response.getBody();
        assertThat(body.getName()).isEqualTo(orderSaved.getName());
        assertThat(body.getId()).isNotNull();
        assertThat(body.getTotal()).isEqualTo(orderSaved.getTotal());

    }

    @Test
    void shouldGetOrderWithBasicAuthWithAuthorizationAnnotation() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ADMIN")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));
        OrderDetails orderSaved = transactionTemplate.execute(status ->
                orderRepository.save(new OrderDetails(1L, "test", 14, 5, 70)));

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "login", "password").getForEntity(
                "http://localhost:" + port + "/api/v1/orders/other/get/" + orderSaved.getId(), OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // and
        OrderDetails body = response.getBody();
        assertThat(body.getName()).isEqualTo(orderSaved.getName());
        assertThat(body.getId()).isNotNull();
        assertThat(body.getTotal()).isEqualTo(orderSaved.getTotal());

    }

    @Test
    void shouldNotGetOrderWithWrongLoginAnnotation() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ADMIN")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));
        OrderDetails orderSaved = transactionTemplate.execute(status ->
                orderRepository.save(new OrderDetails(1L, "test", 14, 5, 70)));

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "wrongLogin", "password").getForEntity(
                "http://localhost:" + port + "/api/v1/orders/other/get/" + orderSaved.getId(), OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void shouldNotGetOrderIfNotAdmin() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "USER")));
        transactionTemplate.execute(status -> userRepository.save(
                new CustomUser(1L, "name", "login", new BCryptPasswordEncoder().encode("password"), role)));
        OrderDetails orderSaved = transactionTemplate.execute(status ->
                orderRepository.save(new OrderDetails(1L, "test", 14, 5, 70)));

        // when
        ResponseEntity<OrderDetails> response = restTemplate.withBasicAuth(
                "login", "password").getForEntity(
                "http://localhost:" + port + "/api/v1/orders/get/" + orderSaved.getId(), OrderDetails.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
