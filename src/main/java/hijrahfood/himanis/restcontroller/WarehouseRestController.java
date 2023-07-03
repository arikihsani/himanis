package hijrahfood.himanis.restcontroller;

import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.DTO.WarehouseDTO;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.model.WarehouseModel;
import hijrahfood.himanis.service.ProcessingService;
import hijrahfood.himanis.service.WarehouseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WarehouseRestController {

    @Autowired
    WarehouseRestService warehouseRestService;

    @Autowired
    ProcessingService processingService;

    @GetMapping("/listWarehouse")
    private List<WarehouseModel> retrieveListWarehouse() { return warehouseRestService.retrieveAllWarehouses(); }

    @PostMapping("/add")
    private ProcessingModel createProcessing(@RequestBody ProcessingDTO processingDTO, Authentication authentication) {
        return processingService.processingRequest(processingDTO, authentication);
    }
}
