package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;

import java.util.List;

public interface ForecastingRestService {
    List<ProductReportDTO> getProductReports(Integer months);
}
