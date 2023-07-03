package hijrahfood.himanis.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.FinishingModel;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import hijrahfood.himanis.model.ProcessingModel;

//import static jdk.xml.internal.SecuritySupport.getClassLoader;
import static org.apache.poi.ss.util.CellUtil.createCell;
import static org.apache.poi.ss.util.CellUtil.getRow;

public class ExcelGenerator {
    private List<BahanModel> listBahan;
    private ProcessingModel processing;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelGenerator(List<BahanModel>  listBahan, ProcessingModel processing) {
        this.listBahan =  listBahan;
        this.processing = processing;
        workbook = new XSSFWorkbook();
    }

    private void writeTopInformation() throws IOException {
        sheet = workbook.createSheet("Pekerjaan Pesanan");

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
        InputStream in = ExcelGenerator.class.getClassLoader().getResourceAsStream("hijrahfoodlogo.jpeg");
//        byte[] inputImageBytes = IOUtils.toByteArray(in);
//        int inputImageLogo = workbook.addPicture(inputImageBytes, workbook.PICTURE_TYPE_JPEG);
        XSSFDrawing drawing = (XSSFDrawing) sheet.createDrawingPatriarch();
        XSSFClientAnchor logoAnchor = new XSSFClientAnchor();

//        logoAnchor.setCol1(0);
//        logoAnchor.setCol2(1);
//        logoAnchor.setRow1(0);
//        drawing.createPicture(logoAnchor, inputImageLogo);

        Row row = sheet.createRow(0);
        sheet.autoSizeColumn(0);
        Row row1 = sheet.createRow(1);
        row1.setHeight((short)1000);
        Row row2 = sheet.createRow(2);
        Row row4 = sheet.createRow(3);
        Row row5 = sheet.createRow(4);
        Row row6 = sheet.createRow(5);
        Row row7 = sheet.createRow(6);
        row7.setHeight((short)-1);

        CellStyle style1 = workbook.createCellStyle();
        XSSFFont font1 = workbook.createFont();
        font1.setBold(true);
        font1.setFontHeight(20);
        style1.setFont(font1);

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeight(10);
        style2.setFont(font2);
        style2.setWrapText(true);

        CellStyle style3 = workbook.createCellStyle();
        XSSFFont font3 = workbook.createFont();
        font3.setFontHeight(12);
        style3.setFont(font3);
        style3.setAlignment(HorizontalAlignment.LEFT);
        font3.setColor(IndexedColors.WHITE.index);
        style3.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style3.setFillForegroundColor(IndexedColors.DARK_TEAL.index);

        CellStyle style4 = workbook.createCellStyle();
        XSSFFont font4 = workbook.createFont();
        font4.setBold(true);
        font4.setFontHeight(16);
        style4.setFont(font4);
        style4.setBorderTop(BorderStyle.MEDIUM);
        style4.setBorderBottom(BorderStyle.MEDIUM);
        createCell(row2, 2, "", style4);

        CellStyle style5 = workbook.createCellStyle();
        XSSFFont font5 = workbook.createFont();
        font5.setFontHeight(9);
        font5.setBold(true);
        style5.setFont(font5);
        style5.setAlignment(HorizontalAlignment.LEFT);
        style5.setWrapText(true);

        sheet.addMergedRegion(CellRangeAddress.valueOf("B1:I1"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B2:I2"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("B3:C3"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("C4:G4"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("C5:G5"));
        sheet.addMergedRegion(CellRangeAddress.valueOf("C6:G6"));
//        sheet.addMergedRegion(CellRangeAddress.valueOf("C7:G7"));

        String proNumber = processing.getProcessingNumber();
        LocalDate tanggal = processing.getDateCreated();
        Date tanggalFix = java.sql.Date.valueOf(tanggal);

        String desc = processing.getDescription();
        String output = processing.getOutputTarget();

        createCell(row, 1, "PT Hijrah Gizi Hewani", style1);
        createCell(row1, 1, "Jl. Raya Bantar Gebang Setu No. 57, Cimuning\n" +
                "Kota Bekasi Jawa Barat 17155\n" +
                "Indonesia", style2);
        createCell(row2, 1, "Pekerjaan Pesanan", style4);
        createCell(row4, 1, "Nomor # : ", style3);
        createCell(row4, 2, proNumber, style5);
        createCell(row5, 1, "Tanggal :", style3);
        createCell(row5, 2, formatter.format(tanggalFix), style5);
        createCell(row6, 1, "Keterangan :", style3);
        createCell(row6, 2, desc, style5);
        createCell(row7, 1, "Untuk Processing :", style3);
        createCell(row7, 2, output, style5);

    }

    private void writeHeader() {
        Row row = sheet.createRow(8);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setBold(true);
        font.setFontHeight(13);
        font.setColor(IndexedColors.WHITE.index);
        style.setFont(font);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.index);

//        style.setDataFormat(format.getFormat("#.##0,##"));
//        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#.##0"));
//        style.setDataFormat(workbook.createDataFormat().getFormat("#.##0"));

        createCell(row, 1, "Kode Barang", style);
        createCell(row, 2, "Nama Barang", style);
        createCell(row, 3, "Kts", style);
        createCell(row, 4, "Satuan", style);
        createCell(row, 5, "Nilai", style);

    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof String) {
            cell.setCellValue((String) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        else {
            cell.setCellValue((Date) value);
        }
        cell.setCellStyle(style);
    }

    private void write() {
        int rowCount = 9;
        int lastColumnCount = 0;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(11);
        style.setFont(font);
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.##"));

        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setFontHeight(11);
        style2.setFont(font2);
        style2.setDataFormat(format.getFormat("#.##0,##;-#.##0,##"));

        CellStyle style3 = workbook.createCellStyle();
        style3.setBorderTop(BorderStyle.THICK);
        style3.setBorderBottom(BorderStyle.THICK);

        for (BahanModel bahan : listBahan) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 1;
            Long totalCost = Long.valueOf(0);
            totalCost = bahan.getQuantity().longValue() * (bahan.getItem().getCost());
//            totalCost = bahan.getQuantity().longValue() * Long.valueOf(150000);
            createCell(row, columnCount++, bahan.getItem().getCode(), style2);
            createCell(row, columnCount++, bahan.getItem().getName(), style2);
            createCell(row, columnCount++, bahan.getQuantity(), style2);
            createCell(row, columnCount++, bahan.getItem().getUnit(), style);
//            createCell(row, columnCount++, "Kg", style2); // Soon change to getUnit()
            createCell(row, columnCount++, totalCost, style);
            lastColumnCount = rowCount;

        }

        Row rowLast = sheet.createRow(rowCount);
        rowLast.setHeight((short)125);
        createCell(rowLast, 1, "", style3);
        createCell(rowLast, 2, "", style3);
        createCell(rowLast, 3, "", style3);
        createCell(rowLast, 4, "", style3);
        createCell(rowLast, 5, "", style3);

        Long totalPrice = Long.valueOf(0);
        for (BahanModel bahan : listBahan) {
            totalPrice += bahan.getQuantity().longValue() * (bahan.getItem().getCost());
//            totalPrice += bahan.getQuantity().longValue() * Long.valueOf(150000);
        }

        CellStyle style4 = workbook.createCellStyle();
        XSSFFont font4 = workbook.createFont();
        font4.setBold(true);
        font4.setFontHeight(11);
        style4.setFont(font4);
        style4.setDataFormat(format.getFormat("#,##0.##"));
        font4.setColor(IndexedColors.WHITE.index);
        style4.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style4.setFillForegroundColor(IndexedColors.DARK_TEAL.index);
        style4.setAlignment(HorizontalAlignment.LEFT);

        CellStyle style5 = workbook.createCellStyle();
        XSSFFont font5 = workbook.createFont();
        font5.setBold(true);
        font5.setFontHeight(11);
        style5.setFont(font5);
        style5.setDataFormat(format.getFormat("#,##0.##"));
        font5.setColor(IndexedColors.WHITE.index);
        style5.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style5.setFillForegroundColor(IndexedColors.DARK_TEAL.index);
        style5.setAlignment(HorizontalAlignment.RIGHT);

        Row rowTotalPrice = sheet.createRow(rowCount + 1);
        createCell(rowTotalPrice, 4, "Total", style4);
        createCell(rowTotalPrice, 5, totalPrice, style5);

    }

    public void generate(HttpServletResponse response) throws IOException {
        writeTopInformation();
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

}
