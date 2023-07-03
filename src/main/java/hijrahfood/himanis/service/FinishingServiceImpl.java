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
import hijrahfood.himanis.DTO.*;
import hijrahfood.himanis.model.*;
import hijrahfood.himanis.repository.*;
import hijrahfood.himanis.repository.FinishingDb;
import hijrahfood.himanis.repository.HasilDb;
import hijrahfood.himanis.repository.UserDb;
import hijrahfood.himanis.repository.ItemDb;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.swing.border.Border;
import javax.transaction.Transactional;

import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@Transactional
public class FinishingServiceImpl implements FinishingService{

    @Autowired
    FinishingDb finishingDb;

    @Autowired
    UserDb userDb;

    @Autowired
    ItemDb itemDb;

    @Autowired
    HasilDb hasilDb;

    @Autowired
    ProcessingDb processingDb;

    @Autowired
    HasilService hasilService;

    @Autowired
    FinishingService finishingService;

    @Override
    public FinishingModel getFinishingById(Long id){
        Optional<FinishingModel> finishing = finishingDb.findById(id);
        if (finishing.isPresent()) {
            return finishing.get();

        }
        return null;
    }

    @Override
    public void updateFinishing(FinishingModel Finishing){
        finishingDb.save(Finishing);
    }

    @Override
    public List<FinishingModel> getFinishingList(){return finishingDb.findAll();}

    @Override
    public String calculateFinishingNumber() {

        if (finishingDb.count() == 0){
            return "PR" + "/"
                    + String.valueOf(LocalDate.now().getMonthValue()) + "/"
                    + String.valueOf(LocalDate.now().getYear()) + "/"
                    + StringUtils.leftPad(String.valueOf(1), 4, "0");
        }

        else{
            return "PR" + "/"
                    + String.valueOf(LocalDate.now().getMonthValue()) + "/"
                    + String.valueOf(LocalDate.now().getYear()) + "/"
                    + StringUtils.leftPad(String.valueOf(finishingDb.getTopByOrderByIdDesc().getId()+1), 4, "0");
        }
    }

    @Override
    public FinishingModel finishingRequest(FinishingDTO finishingDTO, Authentication authentication) {
        FinishingModel finishing = new FinishingModel();
        finishing.setProcessing(processingDb.getByProcessingNumber(finishingDTO.getProcessingNumber()));
        finishing.setDateCreated(LocalDate.now());
        finishing.setDescription(finishingDTO.getDescription());
        finishing.setAssignee(userDb.findByEmail(authentication.getName()).getName());
        finishing.setFinishingNumber(calculateFinishingNumber());
        finishing = finishingDb.save(finishing);
        List<HasilModel> listHasil = new ArrayList<>();
        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        for (HasilDTO hasil: finishingDTO.getListHasil()) {
            HasilModel hasilModel = new HasilModel();
            ItemModel itemModel = itemDb.findById(hasil.getIdItem()).get();
            hasilModel.setItem(itemModel);
            hasilModel.setQuantity(hasil.getQuantity());
            hasilModel.setPercentage(hasil.getPercentage());
            hasilModel.setFinishing(finishing);
            hasilModel = hasilDb.save(hasilModel);
            listHasil.add(hasilModel);

            linesAttributesDTOList.add(new LinesAttributesDTO(itemModel.getName(), hasilModel.getQuantity()));
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                finishing.getProcessing().getWarehouse().getId().intValue(),
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

        finishing.setHasil(listHasil);
        return finishingDb.save(finishing);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class StockAdjustmentDTOContainer {
        private StockAdjustmentDTO stock_adjustment;
    }

    @Override
    public void deleteFinishing(FinishingModel finishing) {

        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        for (HasilModel hasil : finishing.getHasil()) {
            linesAttributesDTOList.add(new LinesAttributesDTO(hasil.getItem().getName(), hasil.getQuantity() * -1 ));
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                finishing.getProcessing().getWarehouse().getId().intValue(),
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

        finishingDb.delete(finishing);
    }

    @Override
    public ProcessingModel getProcessingByProcessingNum(String processingNumber) {
        return processingDb.getByProcessingNumber(processingNumber);
    }

    @Override
    public List<FinishingModel> getFinishingByDate(Date awal, Date akhir) {
        return finishingDb.filterFinishingByDate(awal, akhir);
    }

    // export

    private void writeTableHeaderHasil(PdfPTable table, Long idFinishing) {
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

        cell.setPhrase(new Phrase("Porsi", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Alokasi Biaya", font));
        table.addCell(cell);

        FinishingModel finishing = finishingService.getFinishingById(idFinishing);
        List<HasilModel> hasil = finishing.getHasil();
        table.getDefaultCell().setBorderWidth(0f);

        List<Long> cost = new ArrayList <>();
        long x = 100;
        long static_price = 150000;

        for (HasilModel item : finishing.getHasil()) {
            table.addCell(String.valueOf(item.getItem().getCode()));
            table.addCell(item.getItem().getName());
            table.addCell(String.valueOf(String.format("%,.2f", item.getQuantity())));
            table.addCell("Kg");
            table.addCell(String.valueOf(String.format("%,.1f", item.getPercentage()) + " %"));

            // nanti ganti x dengan item.getitem.cost
//            cost.add(item.getQuantity().longValue() * x);
            cost.add(item.getQuantity().longValue() * item.getItem().getCost());
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            table.addCell(String.valueOf(formatRupiah.format(item.getQuantity().longValue() * x)));
        }
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

    private void writeTableFinishing(PdfPTable table, Long idFinishing) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        table.getDefaultCell().setBorderWidth(0f);
        cell.setPadding(3);

        Font font_1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        Font font_2 = FontFactory.getFont(FontFactory.HELVETICA);

        cell.setPhrase(new Phrase("Penyelesaian Pesanan", font_1));
        table.addCell(cell);
        table.addCell(" ");
        table.addCell(" ");
        cell.setColspan(1);

        FinishingModel finishing = finishingService.getFinishingById(idFinishing);

        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Nomor #");
        table.addCell(": ");
        table.addCell(String.valueOf(finishing.getFinishingNumber()));
        cell.setColspan(1);

        String strDate = finishing.getDateCreated().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Tanggal");
        table.addCell(": ");
        table.addCell(strDate);
        cell.setColspan(1);

        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Keterangan");
        table.addCell(": ");
        table.addCell(String.valueOf(finishing.getDescription()));
        cell.setColspan(1);

        cell.setPhrase(new Phrase("", font_2));
        table.addCell("Bahan Processing");
        table.addCell(": ");

        int counter = 1;
        String listBahan = "";
        for (BahanModel bahan : finishing.getProcessing().getListBahan()) {
            if (counter == finishing.getProcessing().getListBahan().size()) {
                listBahan += bahan.getItem().getName();
            } else {
                listBahan += bahan.getItem().getName() + ", ";
            }

            counter++;
        }
        table.addCell(listBahan);
        cell.setColspan(1);

        cell.setPhrase(new Phrase("", font_2));
        table.addCell("No. Pesanan");
        table.addCell(": ");
        table.addCell(String.valueOf(finishing.getProcessing().getProcessingNumber()));
        cell.setColspan(1);

    }

    public void exportPDF(HttpServletResponse response, Long idFinishing) throws DocumentException, IOException {
        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());

        doc.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(new Color(1,41,112));

        PdfPTable tableHijrahfood = new PdfPTable(1);
        tableHijrahfood.setWidthPercentage(100f);
        tableHijrahfood.setSpacingBefore(10);

        PdfPTable tableFinishing = new PdfPTable(3);
        tableFinishing.setWidthPercentage(100f);
        tableFinishing.setWidths(new float[] {1.0f, 0.1f, 2.0f});
        tableFinishing.setSpacingBefore(10);

        PdfPTable tableHasil = new PdfPTable(6);
        tableHasil.setWidthPercentage(100f);
        tableHasil.setWidths(new float[] {2f, 3.0f, 0.7f, 1.0f, 1.0f, 2.5f});
        tableHasil.setSpacingBefore(10);

        PdfPTable tableKeterangan = new PdfPTable(1);
        tableKeterangan.setWidthPercentage(100f);
        tableKeterangan.setSpacingBefore(10);

        writeTableHijrahfood(tableHijrahfood);
        writeTableFinishing(tableFinishing, idFinishing);
        writeTableHeaderHasil(tableHasil, idFinishing);

        doc.add(tableHijrahfood);
        doc.add(tableFinishing);
        doc.add(tableHasil);

        doc.close();
    }

}


    

