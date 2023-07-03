package hijrahfood.himanis.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StockAdjustmentDTO {
    private String stock_adjustment_type;
    private Integer warehouse_id;
    private String account_name;
    private Boolean maintain_actual;
    private List<LinesAttributesDTO> lines_attributes;
}
