package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.BahanDTO;
import hijrahfood.himanis.DTO.LinesAttributesDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;
import hijrahfood.himanis.DTO.StockAdjustmentDTO;
import hijrahfood.himanis.model.*;
import hijrahfood.himanis.repository.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Transactional
public class ForecastingServiceImpl implements ForecastingService {
    @Autowired
    ForecastingService forecastingService;

    @Autowired
    ItemDb itemDb;

    @Autowired
    ForecastingProductDb forecastingProductDb;

    @Autowired
    ForecastingRestServiceImpl forecastingRestService;

    @Override
    public List<ForecastingProductModel> getListForecastingProduct() {
        return forecastingProductDb.findAll();
    }

    @Override
    public void updateDataForecastingProduct(Integer months) {
        List<ProductReportDTO> ListProductReport = forecastingRestService.getProductReports(months);
        for (ProductReportDTO productReportDTO : ListProductReport) {
            ForecastingProductModel forecastingProduct= new ForecastingProductModel();
            ItemModel item = itemDb.findItemModelByCode(Long.toString(productReportDTO.getProduct_code()));
            Double productQty = item.getQuantity();

            forecastingProduct.setName(productReportDTO.getProduct_name());
            forecastingProduct.setCode(Long.toString(productReportDTO.getProduct_code()));
            forecastingProduct.setQty(productQty);
            if (productReportDTO.getAverage_quantity() != null) {
                forecastingProduct.setAvgQty(productReportDTO.getAverage_quantity());
            }
            if (productReportDTO.getScale() != null) {
                forecastingProduct.setScale(productReportDTO.getScale());
            }
            forecastingProductDb.save(forecastingProduct);
        }

    }

    @Override
    public ForecastingProductModel getForecastingProductById(Long id) {
        return forecastingProductDb.findForecastingProductModelById(id);
    }

    @Override
    public List<ProductReportDTO> getCustomMonthsProductReport(Integer months) {
        List<ProductReportDTO> ListProductReportCustom = forecastingRestService.getProductReports(months);
        return ListProductReportCustom;
    }

    @Override
    public void deleteAllData() {
        forecastingProductDb.deleteAll();
    }

    @Override
    public void updateDataForecastCustomMonths(Integer months) {
        List<ProductReportDTO> ListProductReport = forecastingRestService.getProductReports(months);
        for (ProductReportDTO productReportDTO : ListProductReport) {
            ForecastingProductModel forecastingProduct = forecastingProductDb.findForecastingProductModelByCode(Long.toString(productReportDTO.getProduct_code()));
            ItemModel item = itemDb.findItemModelByCode(Long.toString(productReportDTO.getProduct_code()));
            Double productQty = item.getQuantity();

            forecastingProduct.setQty(productQty);
            if (productReportDTO.getAverage_quantity() != null) {
                forecastingProduct.setAvgQty(productReportDTO.getAverage_quantity());
            }
            if (productReportDTO.getScale() != null) {
                forecastingProduct.setScale(productReportDTO.getScale());
            }
            forecastingProductDb.save(forecastingProduct);
        }
    }

    @Override
    public void updateRemarks(ForecastingProductModel forecastingProduct) {
        forecastingProductDb.save(forecastingProduct);
    }


}
