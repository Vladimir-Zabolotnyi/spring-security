package zabolotnyi.springsecurity.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/security")
public class SecurityResource {

    @Autowired
    UserService userService;

    @Autowired
    RoleService roleService;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostMapping("/role/create")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody CustomRole createRole(@RequestBody CustomRole role){
      return  roleService.createRole((role));
    }

    @PostMapping("/user/create")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody CustomUser createUser(@RequestBody CustomUser user){
        logger.info("user To save " + user);
        return userService.createUser(user);
    }

    @GetMapping("/user/get")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody CustomUser getUser(){
        return userService.getCurrentUser();
    }
}
