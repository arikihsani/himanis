package hijrahfood.himanis.service;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import com.lowagie.text.DocumentException;
import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletResponse;

public interface FinishingService {
    FinishingModel getFinishingById(Long id);
    void updateFinishing(FinishingModel finishing);
    List<FinishingModel> getFinishingList();
    String calculateFinishingNumber();
    FinishingModel finishingRequest(FinishingDTO finishingDTO, Authentication authentication);
    void deleteFinishing(FinishingModel finishing);
    ProcessingModel getProcessingByProcessingNum (String processingNumber);
    List<FinishingModel> getFinishingByDate(Date awal, Date akhir);

    void exportPDF(HttpServletResponse response, Long idFinishing) throws DocumentException, IOException;
}
