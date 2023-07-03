package hijrahfood.himanis.restcontroller;

import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.service.ProcessingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ProcessingRestController {
    @Autowired
    ProcessingRestService processingRestService;

    @GetMapping("/processing/{id}")
    private ProcessingModel getProcessing(@PathVariable("id") Long id) {
        return processingRestService.getProcessingById(id);
    }

    @PutMapping("/processing/{id}")
    private ProcessingModel updateProcessing(@PathVariable("id") Long id,
                                             @RequestBody ProcessingDTO processingDTO,
                                             Authentication authentication) {
        return processingRestService.processingUpdate(id, processingDTO, authentication);
    }
}
