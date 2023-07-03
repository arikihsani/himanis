package hijrahfood.himanis.restcontroller;

import hijrahfood.himanis.DTO.SalesReportDTO.ResponseWrapperDTO;
import hijrahfood.himanis.service.ForecastingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ForecastingRestController {

    @Autowired
    ForecastingRestService forecastingRestService;

    @GetMapping("/reports")
    private ResponseWrapperDTO getProductReports(@RequestParam(defaultValue = "2") Integer months) {
        return new ResponseWrapperDTO(forecastingRestService.getProductReports(months));
    }
}
