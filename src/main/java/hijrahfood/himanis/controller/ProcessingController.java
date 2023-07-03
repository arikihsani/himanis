package hijrahfood.himanis.controller;

import com.lowagie.text.DocumentException;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import hijrahfood.himanis.util.ExcelGenerator;

@Controller
@RequestMapping("/processing")
public class ProcessingController {
    @Qualifier("processingServiceImpl")
    @Autowired
    private ProcessingService processingService;

    @Autowired
    private ItemRestService itemRestService;

    @Autowired
    private BahanService bahanService;

    @Autowired
    private ProcessingRestService processingRestService;

    @Autowired
    private LogService logService;


    @GetMapping("/view-all")
    private String pekerjaanPesanan(Model model) {
        List<ProcessingModel> listProcessing = processingService.getProcessingList();
        List <ProcessingModel> selectedProcessing = new ArrayList<>();
        for (ProcessingModel pekerjaan : listProcessing){
            if (pekerjaan.getApproved() == true){
                selectedProcessing.add(pekerjaan);
            }
        }
        model.addAttribute("listProcessing", selectedProcessing);
        return "view-all-pekerjaanPesanan";
    }

    @PostMapping(value = "/view-all")
    private String detailPekerjaanFilterByDate(@RequestParam(required = false, name = "awal") Date awal,
                                               @RequestParam(required = false, name = "akhir") Date akhir,
                                               Model model) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        String awal_s = formatter.format(awal);
        String akhir_s = formatter.format(akhir);
        List<ProcessingModel> listProcessingByDate = processingService.getProcessingByDate(awal, akhir);
        model.addAttribute("listProcessingByDate", listProcessingByDate);
        ArrayList<ProcessingModel>selectedProcessing = new ArrayList<>();
        for (ProcessingModel pekerjaanPesanan: listProcessingByDate){
            if (pekerjaanPesanan.getApproved()==true){
                selectedProcessing.add(pekerjaanPesanan);
            }
        }
        model.addAttribute("listProcessingByDate", selectedProcessing);
        model.addAttribute("awal", awal_s);
        model.addAttribute("akhir", akhir_s);

        return "view-all-pekerjaanPesanan-byDate";
    }

    @GetMapping("/detail")
    private String detailPekerjaanPesanan(@RequestParam(value = "id") Long id, Model model) {
        ProcessingModel processing = processingService.getProcessingById(id);
        List<BahanModel> listBahanPekerjaanPesanan = processing.getListBahan();
        String namaPembuat = processing.getAssignee();
        List<Long> listTotalCost = new ArrayList<>();

        for (BahanModel bahan : listBahanPekerjaanPesanan) {
            Long totalCost = Long.valueOf(0);
            totalCost = bahan.getQuantity().longValue() * Long.valueOf(bahan.getItem().getCost());
//            totalCost = bahan.getQuantity().longValue() * Long.valueOf(150000);
            listTotalCost.add(totalCost);
        }

        model.addAttribute("id", id);
        model.addAttribute("listTotalCost", listTotalCost);
        model.addAttribute("processing", processing);
        model.addAttribute("id", processing.getId());
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        model.addAttribute("listBahanPekerjaanPesanan", listBahanPekerjaanPesanan);
        model.addAttribute("namaPembuat", namaPembuat);
        model.addAttribute("logs", logService.getLogsByProcessing(processing));

        return "detail-pekerjaanPesanan";
    }

    @GetMapping("/detail-approval")
    private String detailApproval(@RequestParam(value = "id") Long id, Model model) {
        ProcessingModel processing = processingService.getProcessingById(id);
        List<BahanModel> listBahanPekerjaanPesanan = processing.getListBahan();
        String namaPembuat = processing.getAssignee();
        List<Long> listTotalCost = new ArrayList<>();

        for (BahanModel bahan : listBahanPekerjaanPesanan) {
            Long totalCost = Long.valueOf(0);
            totalCost = bahan.getQuantity().longValue() * Long.valueOf(bahan.getItem().getCost());
//            totalCost = bahan.getQuantity().longValue() * Long.valueOf(150000);
            listTotalCost.add(totalCost);
        }

        model.addAttribute("id", id);
        model.addAttribute("listTotalCost", listTotalCost);
        model.addAttribute("processing", processing);
        model.addAttribute("id", processing.getId());
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        model.addAttribute("listBahanPekerjaanPesanan", listBahanPekerjaanPesanan);
        model.addAttribute("namaPembuat", namaPembuat);

        return "detail-approval";
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deletePekerjaanPesanan(@PathVariable Long id,Model model, RedirectAttributes red) {

        ProcessingModel processing = processingService.getProcessingById(id);
        String idProcess = processing.getProcessingNumber();
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        processingService.deleteProcessing(processing);
        red.addFlashAttribute("pesanSuccess", "Pekerjaan Pesanan " + "#" + idProcess + " berhasil dihapus!");
        return "redirect:/processing/view-all";
    }

    @RequestMapping(value = "/delete-approval/{id}", method = RequestMethod.GET)
    public String deleteApproval(@PathVariable Long id,Model model, RedirectAttributes red) {

        ProcessingModel processing = processingService.getProcessingById(id);
        String idProcess = processing.getProcessingNumber();
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        processingService.deleteProcessing(processing);
        red.addFlashAttribute("pesanSuccess", "Approval Pekerjaan Pesanan " + "#" + idProcess + " berhasil dihapus!");
        return "redirect:/processing/approval";
    }


    @GetMapping("/add")
    public String addProcessing(Model model, @CookieValue(name = "warehouseId", required = false) String warehouseId) {
        if (warehouseId == null) model.addAttribute("listItem", itemRestService.getAllItems());
        else model.addAttribute("listItem", itemRestService.getAllItemsByWarehouseId(warehouseId));
        model.addAttribute("listOutput", itemRestService.getAllItems());
        return "form-add-processing";
    }

    @PostMapping(value = "/add")
    public String addProcessingSubmit(
            Authentication authentication,
            @RequestBody ProcessingDTO processingDTO,
            Model model
    ) {
        return "view-all-pekerjaanPesanan";
    }

    @GetMapping("/update/{id}")
    public String updateProcessing(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        String[] outputTarget = processingService.getProcessingById(id).getOutputTarget().split(", ");
        model.addAttribute("outputTarget", outputTarget);
        model.addAttribute("listItem", itemRestService.getAllItems());
        return "form-update-processing";
    }

    @PostMapping(value = "/update/{id}")
    public String updateProcessingSubmit(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody ProcessingDTO processingDTO,
            Model model
    ) {
        processingRestService.processingUpdate(id, processingDTO, authentication);
        return "form-update-processing";
    }

    @GetMapping("/export/excel")
    public void exportIntoExcel(HttpServletResponse response,
                                @RequestParam(value = "id") Long id,
                                Model model) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = dateFormatter.format(new java.util.Date());

        ProcessingModel processing = processingService.getProcessingById(id);
        List<BahanModel> listBahanPekerjaanPesanan = processing.getListBahan();
        String proNum = processing.getProcessingNumber();

        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=PekerjaanPesanan_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=PekerjaanPesanan_" + proNum + ".xlsx";
        response.setHeader(headerKey, headerValue);

        ExcelGenerator generator = new ExcelGenerator(listBahanPekerjaanPesanan, processing);
        model.addAttribute("id", id);
        generator.generate(response);
    }

    @GetMapping("/update-approval/{id}")
    public String updateApproval(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        String[] outputTarget = processingService.getProcessingById(id).getOutputTarget().split(", ");
        model.addAttribute("outputTarget", outputTarget);
        model.addAttribute("listItem", itemRestService.getAllItems());
        return "form-update-approval";
    }

    @PostMapping(value = "/update-approval/{id}")
    public String updateApprovalubmit(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody ProcessingDTO processingDTO,
            Model model
    ) {
        processingRestService.processingUpdate(id, processingDTO, authentication);
        return "form-update-approval";
    }


    @GetMapping("/approval")
    public String approveProcessing(Model model) {
        List <ProcessingModel> listProcessing = processingService.getProcessingList();
        List <ProcessingModel> selectedProcessing = new ArrayList<>();
        for (ProcessingModel pekerjaan : listProcessing){
            if (pekerjaan.getApproved() == false){
                selectedProcessing.add(pekerjaan);
            }
        }
        model.addAttribute("listProcessing", selectedProcessing);
        return "view-all-approval";
    }

    @RequestMapping(value = "/approved/{id}", method = RequestMethod.GET)
    public String approvedPekerjaanPesanan(@PathVariable Long id,Model model, RedirectAttributes red, Authentication authentication) {

        ProcessingModel processing = processingService.getProcessingById(id);
        String idProcess = processing.getProcessingNumber();
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        processing.setApproved(true);
        processingService.updateProcessing(processing, "Approval", authentication);
        red.addFlashAttribute("pesanSuccess", "Pekerjaan Pesanan " + "#" + idProcess + " telah disetujui!");
        return "redirect:/processing/approval";
    }

    @PostMapping(value = "/update-status/{id}")
    public String updateStatusPekerjaanPesanan(@PathVariable Long id,Model model, RedirectAttributes red, Authentication authentication,
    @ModelAttribute ProcessingModel processing) {

        String idProcess = processing.getProcessingNumber();
        model.addAttribute("processingNumber", processing.getProcessingNumber());
        processingService.updateProcessing(processing, "Update", authentication);
        red.addFlashAttribute("pesanSuccess", "Status Pekerjaan Pesanan " + "#" + idProcess + " berhasil diubah!");
        return "redirect:/processing/view-all";
    }

    @RequestMapping(value = "/export/pdf/{idProcessing}", method = RequestMethod.GET)
    public void exportToPDF (HttpServletResponse response,
                             @PathVariable Long idProcessing
    ) throws DocumentException, IOException {
        response.setContentType("application/pdf");

        // Get current date time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Custom format if needed
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format LocalDateTime
        String formattedDateTime = currentDateTime.format(formatter);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pekerjaan_pesanan_" + formattedDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        processingService.exportPDF(response, idProcessing);
    }

}
