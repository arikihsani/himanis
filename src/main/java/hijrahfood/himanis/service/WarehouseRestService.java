package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.WarehouseDTO;
import hijrahfood.himanis.model.WarehouseModel;

import java.util.List;

public interface WarehouseRestService {
    void refreshWarehouse();
    List<WarehouseModel> retrieveAllWarehouses();
    List<WarehouseModel> getWarehouseList();
}
