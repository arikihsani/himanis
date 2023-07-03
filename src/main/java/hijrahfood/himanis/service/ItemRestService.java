package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.ItemDTO;
import hijrahfood.himanis.model.ItemModel;

import java.util.List;

public interface ItemRestService {
    List<ItemDTO> retrieveAllItems();
    List<ItemModel> getAllItems();
    List<ItemModel> getAllItemsByWarehouseId(String warehouseId);
    ItemModel getItemModelById(Long id);
}
