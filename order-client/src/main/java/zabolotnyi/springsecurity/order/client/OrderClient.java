package zabolotnyi.springsecurity.order.client;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import javax.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import zabolotnyi.springsecurity.OrderDetails;

@Headers("Content-Type: application/json")
public interface OrderClient {

    @RequestLine("GET /api/v1/orders/get/{orderNumber}")
    @PreAuthorize("ROLE_ACC_SERVICE")
    OrderDetails getOrderById(@Param("orderNumber") Long orderNumber);
}
