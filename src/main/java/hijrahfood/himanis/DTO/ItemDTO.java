package hijrahfood.himanis.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemDTO {
    private Long id;
    private String name;
    private String product_code;
    private Double last_buy_price;
    private Double quantity;
    private UnitDTO unit;
    private Map<String, Object> warehouses;
}
