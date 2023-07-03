package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.SalesReportDTO.ProductDTO;
import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;
import hijrahfood.himanis.DTO.SalesReportDTO.ProductWrapperDTO;
import hijrahfood.himanis.DTO.SalesReportDTO.RequestWrapperDTO;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.repository.ItemDb;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static hijrahfood.himanis.rest.Setting.apiKey;
import static hijrahfood.himanis.rest.Setting.jurnalApiUrl;

@Service
@Transactional
public class ForecastingRestServiceImpl implements ForecastingRestService {

    @Autowired
    ItemDb itemDb;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public List<ProductReportDTO> getProductReports(Integer months) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(months);

        RequestWrapperDTO productReportsAPI = Unirest.get(jurnalApiUrl + "/sales_by_products?start_date=" + formatter.format(start) + "&end_date=" + formatter.format(end))
                .header("apikey", apiKey)
                .asObject(RequestWrapperDTO.class)
                .getBody();

        List<ProductReportDTO> productReportDTOList = new ArrayList<>();

        for (ProductWrapperDTO productWrapperDTO : productReportsAPI.getSales_by_products().getReports().getProducts()) {
            try {
                ProductDTO productDTO = productWrapperDTO.getProduct();
                NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
                Number number = null;
                try {
                    number = format.parse(productDTO.getQuantity());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                Long productCode = Long.parseLong(productDTO.getProduct_code());
                Double quantity = number.doubleValue();
                ItemModel item = itemDb.findItemModelByCode(productDTO.getProduct_code());
                Double averageQuantity = quantity / months.doubleValue();
                Double scale = item.getQuantity() / averageQuantity;
                productReportDTOList.add(new ProductReportDTO(productCode, productDTO.getProduct_name(), quantity, averageQuantity, scale));
            } catch (EntityNotFoundException ignored) {
            }

        }

        System.out.println(months);
        System.out.println(start);
        System.out.println(end);
        return productReportDTOList;
    }
}
