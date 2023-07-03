package hijrahfood.himanis.controller;

import com.lowagie.text.DocumentException;
import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;
import hijrahfood.himanis.model.*;
import hijrahfood.himanis.repository.ItemDb;
import hijrahfood.himanis.service.*;
import hijrahfood.himanis.util.ExcelGenerator;
import hijrahfood.himanis.util.ExcelGeneratorPenyelesaian;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;

@Controller
@RequestMapping("/forecasting")
public class ForecastingController {
    @Qualifier("forecastingServiceImpl")
    @Autowired
    private ForecastingService forecastingService;

    @Autowired
    private ItemDb itemDb;

    @GetMapping("/produk-akhir")
    public String forecastProdukAkhir(Model model) {
        Boolean defaultMonths = true;
        forecastingService.updateDataForecastingProduct(2);
        List<ForecastingProductModel> listForecastingProduct = forecastingService.getListForecastingProduct();
        List<String> listUnit = new ArrayList<>();

        for (ForecastingProductModel fp : listForecastingProduct) {
            ItemModel item = itemDb.findItemModelByCode(fp.getCode());
            listUnit.add(item.getUnit());

        }
        for (int i = 0; i < listUnit.size(); i++) {
            if (listUnit.get(i) == null) {
                listUnit.set(i, "--");
            }
        }

        model.addAttribute("listUnit", listUnit);
        model.addAttribute("listForecastingProduct", listForecastingProduct);
        model.addAttribute("months", "2");

        return "Forecasting-produkAkhir";
    }

    @PostMapping("/produk-akhir")
    public String forecastProdukAkhirCustomMonths(@RequestParam(name = "months", defaultValue = "2") Integer months,
                                       Model model) {
        forecastingService.deleteAllData();
        forecastingService.updateDataForecastingProduct(months);
        List<ForecastingProductModel> listForecastingProduct = forecastingService.getListForecastingProduct();
        List<String> listUnit = new ArrayList<>();

        for (ForecastingProductModel fp : listForecastingProduct) {
            ItemModel item = itemDb.findItemModelByCode(fp.getCode());
            listUnit.add(item.getUnit());

        }
        for (int i = 0; i < listUnit.size(); i++) {
            if (listUnit.get(i) == null) {
                listUnit.set(i, "--");
            }
        }
        model.addAttribute("listUnit", listUnit);
        model.addAttribute("listForecastingProduct", listForecastingProduct);

        model.addAttribute("months", months);
        return "Forecasting-produkAkhir";
    }

    @GetMapping("/detail")
    public String detailForecastProdukAkhir(@RequestParam(value = "id") Long id, Model model) {

        ForecastingProductModel forecastProd = forecastingService.getForecastingProductById(id);
        ItemModel item = itemDb.findItemModelByCode(forecastProd.getCode());
        String unit = item.getUnit();

        model.addAttribute("unit", unit);
        model.addAttribute("id", id);
        model.addAttribute("item", forecastProd);
        return "detail-forecastProduk";
    }

    @PostMapping(value = "/update-remarks/{id}")
    public String updateRemarksPost(@PathVariable Long id,Model model, RedirectAttributes red
                                ,@ModelAttribute ForecastingProductModel forecastingProduct) {

        forecastingService.updateRemarks(forecastingProduct);
        String itemName = forecastingProduct.getName();
        red.addFlashAttribute("success", "Remarks berhasil diupdate untuk produk " + itemName);

        return "redirect:/forecasting/detail/?id=" + id;
    }

}
