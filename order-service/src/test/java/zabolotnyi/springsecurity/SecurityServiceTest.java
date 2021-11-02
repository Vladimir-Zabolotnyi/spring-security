package zabolotnyi.springsecurity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import zabolotnyi.springsecurity.security.CustomRole;
import zabolotnyi.springsecurity.security.CustomUser;
import zabolotnyi.springsecurity.security.RoleRepository;
import zabolotnyi.springsecurity.security.RoleService;
import zabolotnyi.springsecurity.security.UserRepository;
import zabolotnyi.springsecurity.security.UserService;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


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
    public void shouldCreateUser() {

        // given
        CustomRole role = transactionTemplate.execute(status -> roleRepository.save(new CustomRole(1L, "ROLE_USER")));

        // and
        CustomUser userToSave = new CustomUser(1L, "name", "login", "password", role);

        // when
        CustomUser userSaved = userService.createUser(userToSave);

        // then
        assertThat(userSaved.getRole().getName()).isEqualTo(userToSave.getRole().getName());
        assertTrue(bCryptPasswordEncoder.matches(userToSave.getPassword(),userSaved.getPassword()));
        assertThat(userSaved.getName()).isEqualTo(userToSave.getName());
        assertThat(userSaved.getLogin()).isEqualTo(userToSave.getLogin());
    }

    @Test
    public void shouldCreateRole() {

        // given
        CustomRole roleToSave = new CustomRole(1L, "ROLE_USER");

        // when
        CustomRole roleSaved = roleService.createRole(roleToSave);

        // then
        assertThat(roleSaved.getName()).isEqualTo(roleToSave.getName());
    }
}
