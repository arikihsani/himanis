package hijrahfood.himanis.DTO.SalesReportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseWrapperDTO {
    List<ProductReportDTO> result;
}
