package zabolotnyi.springsecurity;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import zabolotnyi.springsecurity.security.CustomRole;
import zabolotnyi.springsecurity.security.CustomUser;
import zabolotnyi.springsecurity.security.RoleRepository;
import zabolotnyi.springsecurity.security.UserRepository;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityResourceTest {

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

    @BeforeEach
    void setUp() {

        // setup
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                userRepository.deleteAllInBatch();
                roleRepository.deleteAllInBatch();
            }
        });
    }

    @Test
    void shouldCreateUserWithoutAuth() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ROLE_USER")));

        // and
        CustomUser userToSave = new CustomUser(1L, "name", "login", "password", role);

        // and
        HttpEntity<CustomUser> httpEntity = new HttpEntity<>(userToSave);
        System.out.println(httpEntity);

        // when
//        ResponseEntity<CustomUser> response = restTemplate.exchange("http://localhost:" + port + "/api/v1/security/user/create", HttpMethod.POST, httpEntity, CustomUser.class);
        ResponseEntity<CustomUser> response = restTemplate.postForEntity("http://localhost:" + port + "/api/v1/security/user/create", userToSave, CustomUser.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // and
        CustomUser body = response.getBody();
        assertThat(body.getLogin()).isEqualTo(userToSave.getLogin());
        assertThat(body.getName()).isEqualTo(userToSave.getName());
    }
}
