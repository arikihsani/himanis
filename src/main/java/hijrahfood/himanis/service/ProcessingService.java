package hijrahfood.himanis.service;
import java.io.IOException;
import com.lowagie.text.DocumentException;
import javax.servlet.http.HttpServletResponse;

import java.sql.Date;
import java.util.List;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.DTO.ProcessingDTO;
import org.springframework.security.core.Authentication;

public interface ProcessingService {
    ProcessingModel getProcessingById(Long id);
    void updateProcessing(ProcessingModel processing, String type, Authentication authentication);
    List<ProcessingModel> getProcessingList();
    void deleteProcessing(ProcessingModel processing);
    String calculateProcessingNumber();
    ProcessingModel processingRequest(ProcessingDTO processingDTO, Authentication authentication);
    List<ProcessingModel> getProcessingByDate(Date awal, Date akhir);
    void exportPDF(HttpServletResponse response, Long idFinishing) throws DocumentException, IOException;

}

