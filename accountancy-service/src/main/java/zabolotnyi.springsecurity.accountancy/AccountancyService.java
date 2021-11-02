package zabolotnyi.springsecurity.accountancy;

import javax.annotation.security.DeclareRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Role;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import zabolotnyi.springsecurity.OrderRepository;
import zabolotnyi.springsecurity.OrderService;
import zabolotnyi.springsecurity.order.client.OrderClient;

@Service
@DeclareRoles("ROLE_ACC_SERVICE")
public class AccountancyService {

   @Autowired
    OrderClient orderClient;

    public Integer moneyToPay(Long id) {
        return orderClient.getOrderById(id).getTotal();
    }

//    public Integer moneyToPayWithoutPerm(String name) {
//        return orderService.getOrderByName(name).getTotal();
//    }
}
