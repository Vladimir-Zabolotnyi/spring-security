package zabolotnyi.springsecurity.security;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<CustomUser, Long> {
    Optional<CustomUser> findUserByName(String name);

    Optional<CustomUser> findUserByLogin(String login);
}
