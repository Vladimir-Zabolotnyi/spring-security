package zabolotnyi.springsecurity.security;

import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    public CustomUser getCurrentUser() {
        return (CustomUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public CustomUser createUser(CustomUser user) {
        CustomUser userSaved = userRepository.save(new CustomUser(user.getId(), user.getName(), user.getLogin(), bCryptPasswordEncoder.encode(user.getPassword()), user.getRole()));
        logger.info("created " + userSaved);
        return userSaved;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if (Objects.isNull(login) || login.isEmpty()) {
            throw new IllegalArgumentException("login is null");
        }
        final Optional<CustomUser> user = userRepository.findUserByLogin(login);
        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
