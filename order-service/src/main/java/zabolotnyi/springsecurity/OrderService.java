package zabolotnyi.springsecurity;

import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @RolesAllowed("ROLE_ACC_SERVICE")
    public OrderDetails getOrderById(Long id) {
        return orderRepository.findById(id).get();
    }

    @RolesAllowed("ROLE_NO_ACC_SERVICE")
    public OrderDetails getOrderByName(String name) {
        return orderRepository.findOrderDetailsByName(name);
    }

    public OrderDetails postOrder(OrderView order) {
        return orderRepository.save(
                new OrderDetails(0L, order.getName(), order.getPrice(), order.getQuantity(),
                        order.getPrice() * order.getQuantity())
        );
    }
}
