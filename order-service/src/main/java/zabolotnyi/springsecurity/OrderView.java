package zabolotnyi.springsecurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderView {
    private String name;
    private Integer price;
    private Integer quantity;
}

