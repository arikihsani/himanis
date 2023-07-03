package hijrahfood.himanis.DTO.SalesReportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductReportDTO {
    private Long product_code;
    private String product_name;
    private Double quantity;
    private Double average_quantity;
    private Double scale;
}
