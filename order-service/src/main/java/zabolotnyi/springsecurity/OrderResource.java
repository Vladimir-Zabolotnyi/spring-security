package zabolotnyi.springsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/orders")
public class OrderResource {

    @Autowired
    OrderService orderService;

    @GetMapping("/get/{id}")
    public OrderDetails getOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/other/get/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("authenticated")
    public OrderDetails getOtherOrderById(@PathVariable("id") Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetails createOrder(@RequestBody OrderView order) {
        return orderService.postOrder(order);
    }
}
