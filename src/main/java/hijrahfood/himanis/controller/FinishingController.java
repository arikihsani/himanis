package hijrahfood.himanis.controller;

import com.lowagie.text.DocumentException;
import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.ProcessingModel;
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
import hijrahfood.himanis.model.HasilModel;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;

@Controller
@RequestMapping("/finishing")
public class FinishingController {
    @Qualifier("finishingServiceImpl")

    @Autowired
    private FinishingService finishingService;

    @Autowired
    private FinishingRestService finishingRestService;

    @Autowired
    private ItemRestService itemRestService;

    @Autowired
    private ProcessingService processingService;

    @Autowired
    private HasilService hasilService;

    @Autowired
    private LogService logService;

    @GetMapping("/view-all")
    private String penyelesaianPesanan(Model model){
        List<FinishingModel> listFinishing = finishingService.getFinishingList();
        model.addAttribute("listFinishing",listFinishing);
        return "listPenyelesaianPesanan";
    }

    @PostMapping(value = "/view-all")
    private String detailPenyelesaianFilterByDate(@RequestParam(required = false, name = "awal") Date awal,
                                               @RequestParam(required = false, name = "akhir") Date akhir,
                                               Model model) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String awal_s = formatter.format(awal);
        String akhir_s = formatter.format(akhir);
        List<FinishingModel> listFinishingByDate = finishingService.getFinishingByDate(awal, akhir);
        model.addAttribute("listFinishingByDate", listFinishingByDate);
        model.addAttribute("awal", awal_s);
        model.addAttribute("akhir", akhir_s);

        return "view-all-penyelesaianPesanan-byDate";
    }


    @GetMapping(value = "/view/{id}")
    public String DetailItem(
            Model model, @PathVariable Long id
    ){
        FinishingModel finishing = finishingService.getFinishingById(id);
        List<HasilModel> hasil = finishing.getHasil();
        ProcessingModel processing = finishingService.getProcessingByProcessingNum(finishing.getProcessing().getProcessingNumber());
        model.addAttribute("listHasilPenyelesaianPesanan", hasil);
        List<Double> cost = new ArrayList <>();
        List<String> gfg = new ArrayList<>();
        int x = 100;
        for (HasilModel item: hasil){
            // nanti ganti x dengan item.getitem.cost
//            cost.add(item.getQuantity() * x);
            cost.add(item.getQuantity() * item.getItem().getCost());
        }
        double static_price = 150000;

        model.addAttribute("processing", processing);
        model.addAttribute("listTotalCost", cost);
        model.addAttribute("finishing", finishing);
        model.addAttribute("static_price", static_price);
        model.addAttribute("logs", logService.getLogsByFinishing(finishing));

        return "detailPenyelesaianPesanan";
    }

    @GetMapping("/add")
    public String addFinishing(Model model) {
        List <ProcessingModel> listAll = processingService.getProcessingList();
        ArrayList<ProcessingModel> selectedProcessing = new ArrayList<>();
        for (ProcessingModel pekerjaanPesanan: listAll){
            if (pekerjaanPesanan.getStatus()>1){
                selectedProcessing.add(pekerjaanPesanan);
            }
        }
        model.addAttribute("listProcessing", selectedProcessing);
        model.addAttribute("listItem", itemRestService.getAllItems());
        return "form-add-penyelesaian";
    }

    @PostMapping(value = "/add")
    public String addFinishingSubmit(
            Authentication authentication,
            @RequestBody FinishingDTO finishingDTO,
            Model model
    ) {

        return "viewall-finishing";
    }

    @GetMapping("/update/{id}")
    public String updatefinishing(
        @PathVariable Long id,
        Model model
    ){
        FinishingModel finishing = finishingService.getFinishingById(id);
        model.addAttribute("finishing", finishing);
        model.addAttribute("id", id);
	    model.addAttribute("listItem", itemRestService.getAllItems());
        return "form-update-penyelesaian";
    }

    @PostMapping("/update/{id}")
    public String updatefinishingSubmit(
        @PathVariable Long id,
        Authentication authentication,
        @RequestBody FinishingDTO finishingDTO,
        Model model
    ){
        finishingRestService.finishingUpdate(id, finishingDTO, authentication);
        return "form-update-penyelesaian";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deletePenyelesaianPesanan(@PathVariable Long id, Model model, RedirectAttributes red) {

        FinishingModel finishing = finishingService.getFinishingById(id);
        String idFinishing = finishing.getFinishingNumber();
        model.addAttribute("finishingNumber", finishing.getFinishingNumber());
        finishingService.deleteFinishing(finishing);
        red.addFlashAttribute("pesanSuccess", "Penyelesaian Pesanan " + "#" + idFinishing + " berhasil dihapus!");
        return "redirect:/finishing/view-all";
    }


    @GetMapping("/export/excel")
    public void exportIntoExcel(HttpServletResponse response,
                                @RequestParam(value = "id") Long id,
                                Model model) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new java.util.Date());

        FinishingModel finishing = finishingService.getFinishingById(id);
        List<HasilModel> listHasil = finishing.getHasil();
        String penyelesaianNum = finishing.getFinishingNumber();

        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=PekerjaanPesanan_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=PenyelesaianPesanan_" + penyelesaianNum + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelGeneratorPenyelesaian generator = new ExcelGeneratorPenyelesaian(finishing);
        model.addAttribute("id", id);
        generator.generate(response);
    }

    @RequestMapping(value = "/export/pdf/{idFinishing}", method = RequestMethod.GET)
    public void exportToPDF (HttpServletResponse response,
                             @PathVariable Long idFinishing
    ) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        // Get current date time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Custom format if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format LocalDateTime
        String formattedDateTime = currentDateTime.format(formatter);

        FinishingModel finishing = finishingService.getFinishingById(idFinishing);
        String id = finishing.getFinishingNumber();

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=PenyelesaianPesanan_" + id + "_" + formattedDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        finishingService.exportPDF(response, idFinishing);
    }
}