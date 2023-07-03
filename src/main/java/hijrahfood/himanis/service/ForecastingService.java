package hijrahfood.himanis.service;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import javax.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.util.List;

import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;
import hijrahfood.himanis.model.ForecastingProductModel;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.DTO.ProcessingDTO;
import org.springframework.security.core.Authentication;

public interface ForecastingService {
    List<ForecastingProductModel> getListForecastingProduct();
    void updateDataForecastingProduct(Integer months);
    ForecastingProductModel getForecastingProductById(Long id);
    List<ProductReportDTO> getCustomMonthsProductReport(Integer months);
    void deleteAllData();
    void updateDataForecastCustomMonths(Integer months);
    void updateRemarks(ForecastingProductModel forecastingProduct);
}
