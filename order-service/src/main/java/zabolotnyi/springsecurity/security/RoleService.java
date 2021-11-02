package zabolotnyi.springsecurity.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public CustomRole createRole(CustomRole role) {
        if (role.getName().contains("ROLE_")) {
            return roleRepository.save(role);
        } else throw new RuntimeException();
    }
}
