package hijrahfood.himanis.DTO.SalesReportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RequestWrapperDTO {
    private SalesByProductsDTO sales_by_products;
}
