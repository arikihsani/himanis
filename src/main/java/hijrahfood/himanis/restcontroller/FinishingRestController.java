package hijrahfood.himanis.restcontroller;

import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.service.FinishingRestService;
import hijrahfood.himanis.service.FinishingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class FinishingRestController {

    @Autowired
    FinishingService finishingService;

    @Autowired
    FinishingRestService finishingRestService;

    @PostMapping("/finishing/add")
    private FinishingModel createFinishing(@RequestBody FinishingDTO finishingDTO, Authentication authentication) {
        return finishingService.finishingRequest(finishingDTO, authentication);
    }

    @GetMapping("/finishing/{id}")
    private FinishingModel getFinishing(@PathVariable("id") Long id) {
        return finishingRestService.getFinishingById(id);
    }

    @PutMapping("/finishing/update/{id}")
    private FinishingModel updateFinishing(@PathVariable("id") Long id,
                                             @RequestBody FinishingDTO finishingDTO,
                                             Authentication authentication) {
        return finishingRestService.finishingUpdate(id, finishingDTO, authentication);
    }

}