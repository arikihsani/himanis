package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.security.core.Authentication;

public interface ProcessingRestService {
    ProcessingModel getProcessingById(Long id);
    ProcessingModel processingUpdate(Long id, ProcessingDTO processingDTO, Authentication authentication);
}
