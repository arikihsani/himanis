package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.model.FinishingModel;
import org.springframework.security.core.Authentication;


public interface FinishingRestService {
    FinishingModel getFinishingById(Long id);
    FinishingModel finishingUpdate(Long id, FinishingDTO finishingDTO, Authentication authentication);
}
