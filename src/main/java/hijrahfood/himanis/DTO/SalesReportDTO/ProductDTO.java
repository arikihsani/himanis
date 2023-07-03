package hijrahfood.himanis.DTO.SalesReportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {
    private String product_code;
    private String product_name;
    private String quantity;

}
