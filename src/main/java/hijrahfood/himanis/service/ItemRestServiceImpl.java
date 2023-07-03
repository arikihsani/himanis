package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.ItemDTO;
import hijrahfood.himanis.DTO.WarehouseDTO;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.model.WarehouseModel;
import hijrahfood.himanis.repository.ItemDb;
import hijrahfood.himanis.repository.WarehouseDb;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hijrahfood.himanis.rest.Setting.apiKey;
import static hijrahfood.himanis.rest.Setting.jurnalApiUrl;

@Service
@Transactional
public class ItemRestServiceImpl implements ItemRestService {
    @Autowired
    ItemDb itemDb;

    @Autowired
    WarehouseDb warehouseDb;

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    class ItemResponse {
        List<ItemDTO> products;
    }

    @Override
    public List<ItemDTO> retrieveAllItems() {
        HttpResponse<WarehouseRestServiceImpl.WarehouseResponse> request = Unirest.get(jurnalApiUrl + "/warehouses")
                .header("apikey", apiKey)
                .asObject(WarehouseRestServiceImpl.WarehouseResponse.class);
        WarehouseRestServiceImpl.WarehouseResponse warehouseDTOList = request
                .getBody();

//        System.out.println(warehouseDTOList);
//        if (warehouseDTOList == null) {
//            UnirestParsingException ex = request.getParsingError().get();
//
//            System.out.println(ex.getOriginalBody()); // Has the original body as a string.
//            System.out.println(ex.getMessage()); // Will have the parsing exception.
//            System.out.println(ex.getCause()); // of course will have the original parsing exception itself.
//        }

        for (WarehouseDTO warehouseDTO : warehouseDTOList.warehouses) {
            if (!warehouseDb.existsById(warehouseDTO.getId())) {
                WarehouseModel warehouse = new WarehouseModel();
                warehouse.setId(warehouseDTO.getId());
                warehouse.setName(warehouseDTO.getName());
                warehouseDb.save(warehouse);
            }
        }

        ItemResponse itemDTOList = Unirest.get(jurnalApiUrl + "/products?page_size=1500")
                .header("apikey", apiKey)
                .asObject(ItemResponse.class)
                .getBody();

        System.out.println(itemDTOList);

        int counter = 0;
        for (ItemDTO itemDTO : itemDTOList.products) {
            if (itemDTO.getWarehouses() != null) {
                for (String warehouseId : itemDTO.getWarehouses().keySet()) {
                    Long warehouseIdLong = Long.valueOf(warehouseId);

                    WarehouseModel warehouseModel = warehouseDb.getById(warehouseIdLong);
                    if (!itemDb.existsByIdAndWarehouse(itemDTO.getId(), warehouseModel)) {
                        ItemModel item = new ItemModel();
                        item.setId(itemDTO.getId());
                        item.setName(itemDTO.getName());
                        item.setCode(itemDTO.getProduct_code());
                        item.setCost(itemDTO.getLast_buy_price().longValue());
                        item.setQuantity(itemDTO.getQuantity());
                        item.setUnit(itemDTO.getUnit().getName());
                        item.setWarehouse(warehouseModel);
                        if (itemDTO.getQuantity() != null) {
                            item.setQuantity(itemDTO.getQuantity());
                        }
                        itemDb.save(item);
                    } else {
                        if (itemDTO.getQuantity() != null) {
                            ItemModel item = itemDb.getById(itemDTO.getId());
                            item.setQuantity(itemDTO.getQuantity());
                            item.setUnit(itemDTO.getUnit().getName());
                        }
                    }
                }
            } else {
                if (!itemDb.existsById(itemDTO.getId())) {
                    ItemModel item = new ItemModel();
                    item.setId(itemDTO.getId());
                    item.setName(itemDTO.getName());
                    item.setCode(itemDTO.getProduct_code());
                    item.setCost(itemDTO.getLast_buy_price().longValue());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setUnit(itemDTO.getUnit().getName());

                    itemDb.save(item);
                } else {
                    if (itemDTO.getQuantity() != null) {
                        ItemModel item = itemDb.getById(itemDTO.getId());
                        item.setQuantity(itemDTO.getQuantity());
                        item.setUnit(itemDTO.getUnit().getName());
                    }
                }
            }
        }
        return itemDTOList.products;
    }

    public List<ItemModel> getAllItemsByWarehouseId(String warehouseId) {
        Long warehouseIdLong = Long.valueOf(warehouseId);
        WarehouseModel warehouseModel = warehouseDb.getById(warehouseIdLong);
        System.out.println(warehouseId);
        return itemDb.findAllByWarehouse(warehouseModel);
    }

    public List<ItemModel> getAllItems() {
        return itemDb.findAll();
    }

    @Override
    public ItemModel getItemModelById(Long id) {
        Optional<ItemModel> item = itemDb.findById(id);
        if (item.isPresent()) {
            return item.get();
        }
        return null;
    }
}
