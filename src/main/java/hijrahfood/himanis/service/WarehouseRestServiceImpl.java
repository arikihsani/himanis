package hijrahfood.himanis.service;

import hijrahfood.himanis.DTO.WarehouseDTO;
import hijrahfood.himanis.model.WarehouseModel;
import hijrahfood.himanis.repository.WarehouseDb;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestParsingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static hijrahfood.himanis.rest.Setting.apiKey;
import static hijrahfood.himanis.rest.Setting.jurnalApiUrl;

@Service
@Transactional
public class WarehouseRestServiceImpl implements WarehouseRestService{

    @Autowired
    private WarehouseDb warehouseDb;

    @Override
    public void refreshWarehouse() {

    }

    @Override
    public List<WarehouseModel> getWarehouseList() { return warehouseDb.findAll(); }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    class WarehouseResponse {
        List<WarehouseDTO> warehouses;
    }

    @Override
    public List<WarehouseModel> retrieveAllWarehouses() {
        return warehouseDb.findAll();
    }
}
