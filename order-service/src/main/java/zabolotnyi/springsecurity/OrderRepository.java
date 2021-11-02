package zabolotnyi.springsecurity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderDetails, Long> {

    public OrderDetails findOrderDetailsByName(String name);
}
