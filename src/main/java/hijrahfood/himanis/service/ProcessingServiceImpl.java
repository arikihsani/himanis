package hijrahfood.himanis.service;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import javax.swing.*;
import javax.swing.border.Border;

import hijrahfood.himanis.DTO.BahanDTO;
import hijrahfood.himanis.DTO.LinesAttributesDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Service
@Transactional
public class ProcessingServiceImpl implements ProcessingService {
    @Autowired
    BahanService bahanService;

    @Autowired
    ProcessingService processingService;

    @Autowired
    BahanDb bahanDb;

    @Autowired
    UserDb userDb;

    @Autowired
    WarehouseDb warehouseDb;

    @Autowired
    ItemDb itemDb;

    @Autowired
    ProcessingDb processingDb;

    @Autowired
    LogDb logDb;

    @Override
    public ProcessingModel getProcessingById(Long id) {
        Optional<ProcessingModel> processing = processingDb.findById(id);
        if (processing.isPresent()) {
            return processing.get();
        }
        return null;
    }

    @Override
    public void updateProcessing(ProcessingModel processing, String type, Authentication authentication) {
        if (type.equals("Update")) {
            LogModel logModel = new LogModel();
            logModel.setDate(LocalDateTime.now());
            logModel.setProcessing(processing);
            logModel.setAssignee(userDb.findByEmail(authentication.getName()).getName());

            switch (processing.getStatus()) {
                case 0:
                    logModel.setActivity("Status (Belum Dikerjakan)");
                    break;
                case 1:
                    logModel.setActivity("Status (Sedang Dikerjakan)");
                    break;
                case 2:
                    logModel.setActivity("Status (Selesai Sebagian)");
                    break;
                case 3:
                    logModel.setActivity("Status (Selesai)");
                    break;
            }
            logDb.save(logModel);
            System.out.println("masuk");
        }
        processingDb.save(processing);
    }

    @Override
    public List<ProcessingModel> getProcessingList() {
        return processingDb.findAll();
    }

    @Override
    public void deleteProcessing(ProcessingModel processing) {
        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        for (BahanModel bahan : processing.getListBahan()) {
            linesAttributesDTOList.add(new LinesAttributesDTO(bahan.getItem().getName(), bahan.getQuantity()));
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                processing.getWarehouse().getId().intValue(),
                "Persediaan Barang", false, linesAttributesDTOList);

        try {
            String _json;
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            _json = ow.writeValueAsString(new StockAdjustmentDTOContainer(stockAdjustmentDTO));
            System.out.println(_json);

            HttpResponse<String> response = Unirest.post("https://api.jurnal.id/core/api/v1/stock_adjustments")
                    .header("apikey", "0275f676e18a35182adef8967145e518")
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*")
                    .body(_json)
                    .asString();

            System.out.println(response.getStatus());
            System.out.println(response.getBody());
            System.out.println(response.getCookies());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        processingDb.delete(processing);
    }

    @Override
    public String calculateProcessingNumber() {

        if (processingDb.count() == 0){
            return "PRO" + "/"
            + String.valueOf(LocalDate.now().getMonthValue()) + "/"
            + String.valueOf(LocalDate.now().getYear()) + "/"
            + StringUtils.leftPad(String.valueOf(1), 4, "0");
        }
        else{
            return "PRO" + "/"
            + String.valueOf(LocalDate.now().getMonthValue()) + "/"
            + String.valueOf(LocalDate.now().getYear()) + "/"
            + StringUtils.leftPad(String.valueOf(processingDb.findTopByOrderByIdDesc().getId()+1), 4, "0");
        }
        
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class StockAdjustmentDTOContainer {
        private StockAdjustmentDTO stock_adjustment;
    }

    @Override
    public ProcessingModel processingRequest(ProcessingDTO processingDTO, Authentication authentication) {
        ProcessingModel processing = new ProcessingModel();
        processing.setDescription(processingDTO.getDescription());
        processing.setStatus(0);
        processing.setOutputTarget(processingDTO.getOutputTarget());
        processing.setWarehouse(warehouseDb.getById(processingDTO.getIdWarehouse()));
        processing.setApproved(false);
        processing.setDateCreated(LocalDate.now());
        processing.setAssignee(userDb.findByEmail(authentication.getName()).getName());
        processing.setProcessingNumber(calculateProcessingNumber());
        processing = processingDb.save(processing);

        List<BahanModel> listBahan = new ArrayList<>();
        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        for (BahanDTO bahan : processingDTO.getListBahan()) {
            BahanModel bahanModel = new BahanModel();
            ItemModel itemModel = itemDb.findById(bahan.getIdItem()).get();
            bahanModel.setItem(itemModel);
            bahanModel.setQuantity(bahan.getQuantity());
            bahanModel.setProcessing(processing);
            bahanModel = bahanDb.save(bahanModel);
            listBahan.add(bahanModel);

            linesAttributesDTOList.add(new LinesAttributesDTO(itemModel.getName(), bahanModel.getQuantity() * -1));
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                processing.getWarehouse().getId().intValue(),
                "Persediaan Barang", false, linesAttributesDTOList);

        try {
            String _json;
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            _json = ow.writeValueAsString(new StockAdjustmentDTOContainer(stockAdjustmentDTO));
            System.out.println(_json);

            HttpResponse<String> response = Unirest.post("https://api.jurnal.id/core/api/v1/stock_adjustments")
                    .header("apikey", "0275f676e18a35182adef8967145e518")
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*")
                    .body(_json)
                    .asString();

            System.out.println(response.getStatus());
            System.out.println(response.getBody());
            System.out.println(response.getCookies());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        processing.setListBahan(listBahan);
        return processingDb.save(processing);
    }

    @Override
    public List<ProcessingModel> getProcessingByDate(Date awal, Date akhir) {
        return processingDb.filterProcessingByDate(awal, akhir);
    }

    // export
    List<BahanModel> listBahan;
    ProcessingModel processing;
    public void ExcelGenerator(List<BahanModel>  listBahan, ProcessingModel processing) {
        this.listBahan =  listBahan;
        this.processing = processing;
    }

    private void writeTableHeaderBahan(PdfPTable table, Long idProcessing) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(new Color(1,41,112));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);

        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("Kode Barang", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nama Barang", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Kts.", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Satuan", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Nilai", font));
        table.addCell(cell);

        ProcessingModel processing = processingService.getProcessingById(idProcessing);
        List<BahanModel> bahan = processing.getListBahan();
        table.getDefaultCell().setBorderWidth(0f);

        Long totalPrice = Long.valueOf(0);
        for (BahanModel bahann : bahan) {
            totalPrice += bahann.getQuantity().longValue() * Long.valueOf(150000);
        }

        int columnCount = 1;

        for (BahanModel item : processing.getListBahan()) {
            Long totalCost = Long.valueOf(0);
            totalCost = item.getQuantity().longValue() * Long.valueOf(150000);
            table.addCell(String.valueOf(item.getItem().getCode()));
            table.addCell(item.getItem().getName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell("Kg");
            table.addCell(String.valueOf(totalCost));

            if(columnCount == processing.getListBahan().size()){
                table.addCell("");
                table.addCell("");
                table.addCell("");
                table.addCell("Total");
                table.addCell(String.valueOf(totalPrice));
            }
            columnCount++;
        }
        System.out.println(columnCount);
        System.out.println(totalPrice);
    }

    private void writeTableHijrahfood(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2);

        Font font_1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font font_2 = FontFactory.getFont(FontFactory.HELVETICA);

        cell.setPhrase(new Phrase("PT Hijrah Gizi Hewani", font_1));
        table.addCell(cell);
        cell.setColspan(1);

        cell.setPhrase(new Phrase("Jl. Raya Bantar Gebang Setu No. 57, Cimuning", font_2));
        table.addCell(cell);
        cell.setColspan(1);

        cell.setPhrase(new Phrase("Kota Bekasi, Jawa Barat, 17155", font_2));
        table.addCell(cell);
        cell.setColspan(1);

        cell.setPhrase(new Phrase("Indonesia", font_2));
        table.addCell(cell);
        cell.setColspan(1);
    }

    private void writeTableProcessing(PdfPTable table, Long idProcessing) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        table.getDefaultCell().setBorderWidth(0f);
        cell.setPadding(3);

        Font font_1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font font_2 = FontFactory.getFont(FontFactory.HELVETICA);

        cell.setPhrase(new Phrase("Pekerjaan Pesanan", font_1));
        table.addCell(cell);
        table.addCell(" ");
        table.addCell(" ");
        cell.setColspan(1);

        ProcessingModel processing = processingService.getProcessingById(idProcessing);

        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Nomor #");
        table.addCell(": ");
        table.addCell(String.valueOf(processing.getProcessingNumber()));
        cell.setColspan(1);

        String strDate = processing.getDateCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Tanggal");
        table.addCell(": ");
        table.addCell(strDate);
        cell.setColspan(1);

        String desc = processing.getDescription();
        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Keterangan");
        table.addCell(": ");
        table.addCell(desc);
        cell.setColspan(1);

        String output = processing.getOutputTarget();
        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Untuk Processing");
        table.addCell(": ");
        table.addCell(output);

    }

    public void exportPDF(HttpServletResponse response, Long idProcessing) throws DocumentException, IOException {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());

        doc.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(new Color(1,41,112));

        PdfPTable tableHijrahfood = new PdfPTable(1);
        tableHijrahfood.setWidthPercentage(100f);
        tableHijrahfood.setSpacingBefore(10);

        PdfPTable tableProcessing = new PdfPTable(3);
        tableProcessing.setWidthPercentage(100f);
        tableProcessing.setWidths(new float[] {1.0f, 0.1f, 2.0f});
        tableProcessing.setSpacingBefore(10);

        PdfPTable tableBahan = new PdfPTable(5);
        tableBahan.setWidthPercentage(100f);
        tableBahan.setWidths(new float[] {2f, 3.5f, 0.7f, 1.0f, 2f});
        tableBahan.setSpacingBefore(10);

        writeTableHijrahfood(tableHijrahfood);
        writeTableProcessing(tableProcessing, idProcessing);
        writeTableHeaderBahan(tableBahan, idProcessing);

        doc.add(tableHijrahfood);
        doc.add(tableProcessing);
        doc.add(tableBahan);

        doc.close();
    }

}
