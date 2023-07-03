package hijrahfood.himanis.restcontroller;

import hijrahfood.himanis.DTO.ItemDTO;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.service.ItemRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ItemRestController {
    @Autowired
    ItemRestService itemRestService;

    @GetMapping("/listItem")
    private List<ItemDTO> retrieveListItem() { return itemRestService.retrieveAllItems(); }

    @GetMapping("/listItemDb")
    private List<ItemModel> getAllItems() { return itemRestService.getAllItems(); }
}
