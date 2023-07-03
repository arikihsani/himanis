package hijrahfood.himanis.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hijrahfood.himanis.DTO.HasilDTO;
import hijrahfood.himanis.DTO.FinishingDTO;
import hijrahfood.himanis.DTO.LinesAttributesDTO;
import hijrahfood.himanis.DTO.StockAdjustmentDTO;
import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.HasilModel;
import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.LogModel;
import hijrahfood.himanis.repository.*;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class FinishingRestServiceImpl implements FinishingRestService {

    @Autowired
    FinishingDb finishingDb;

    @Autowired
    ProcessingDb processingDb;

    @Autowired
    WarehouseDb warehouseDb;

    @Autowired
    UserDb userDb;

    @Autowired
    ItemDb itemDb;

    @Autowired
    HasilDb hasilDb;

    @Autowired

    LogDb logDb;

    public FinishingModel getFinishingById(Long id) {
        return finishingDb.findById(id).get();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class StockAdjustmentDTOContainer {
        private StockAdjustmentDTO stock_adjustment;
    }

    @Override
    public FinishingModel finishingUpdate(Long id, FinishingDTO finishingDTO, Authentication authentication) {
        List<String> activityLog = new ArrayList<>();
        FinishingModel finishing = finishingDb.getById(id);

        if (!finishing.getDescription().equals(finishingDTO.getDescription())){
            activityLog.add("Deskripsi");
            finishing.setDescription(finishingDTO.getDescription());
        } else {
            finishing.setDescription(finishingDTO.getDescription());
        }

        finishing.setProcessing(processingDb.getByProcessingNumber(finishingDTO.getProcessingNumber()));
        finishing.setDescription(finishingDTO.getDescription());
        finishing.setDateCreated(LocalDate.now());
        finishing.setAssignee(userDb.findByEmail(authentication.getName()).getName());
        finishing.setFinishingNumber(finishing.getFinishingNumber());
        finishing = finishingDb.save(finishing);
        List<HasilModel> listHasil = new ArrayList<>();
        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        Map<Long, HasilModel> hasilMap = new HashMap<Long,HasilModel>();
        for (HasilModel i : finishing.getHasil()) hasilMap.put(i.getItem().getId(), i);
        for (HasilDTO hasil: finishingDTO.getListHasil()) {
            if (hasilMap.containsKey(hasil.getIdItem())) {
                if (!hasil.getQuantity().equals(hasilMap.get(hasil.getIdItem()).getQuantity())) {
                    HasilModel hasilModel = hasilMap.get(hasil.getIdItem());
                    linesAttributesDTOList.add(new LinesAttributesDTO(hasilModel.getItem().getName(), hasil.getQuantity() - hasilModel.getQuantity()));
                    if (!Objects.equals(hasilModel.getQuantity(), hasil.getQuantity())){
                        activityLog.add("Hasil");
                        hasilModel.setQuantity(hasil.getQuantity());
                    } else {
                        hasilModel.setQuantity(hasil.getQuantity());
                    }
                    hasilModel.setPercentage(hasil.getPercentage());
                    hasilModel = hasilDb.save(hasilModel);
                    listHasil.add(hasilModel);
                } else {
                    HasilModel hasilModel = hasilMap.get(hasil.getIdItem());
                    hasilModel.setPercentage(hasil.getPercentage());
                    hasilModel = hasilDb.save(hasilModel);
                    listHasil.add(hasilModel);

                }
            } else {
                activityLog.add("Hasil");
                HasilModel hasilModel = new HasilModel();
                hasilModel.setItem(itemDb.getById(hasil.getIdItem()));
                hasilModel.setQuantity(hasil.getQuantity());
                hasilModel.setPercentage(hasil.getPercentage());
                hasilModel.setFinishing(finishing);
                hasilModel = hasilDb.save(hasilModel);
                listHasil.add(hasilModel);

                linesAttributesDTOList.add(new LinesAttributesDTO(itemDb.getById(hasil.getIdItem()).getName(), hasil.getQuantity()));
            }
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                finishing.getProcessing().getWarehouse().getId().intValue(),
                "Persediaan Barang", false, linesAttributesDTOList);

        try {
            String _json;
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            _json = ow.writeValueAsString(new StockAdjustmentDTOContainer(stockAdjustmentDTO));
            System.out.println(_json);

            HttpResponse<String> response = Unirest.post("https://api.jurnal.id/core/api/v1/stock_adjustments")
                    .header("apikey", "0275f676e18a35182adef8967145e518")
                    .header("Content-Type", "application/json")
                    .header("Accept", "*/*")
                    .body(_json)
                    .asString();

            System.out.println(response.getStatus());
            System.out.println(response.getBody());
            System.out.println(response.getCookies());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        List<LogModel> logs = new ArrayList<>();
        LogModel logModel = new LogModel();
        logModel.setDate(LocalDateTime.now());
        logModel.setFinishing(finishing);
        logModel.setAssignee(userDb.findByEmail(authentication.getName()).getName());
        logModel.setActivity(StringUtils.join(activityLog, ", "));
        logDb.save(logModel);

        if (finishing.getLogs().isEmpty()) {
            logs.add(logModel);
        } else {
            for (LogModel log : finishing.getLogs()) {
                logs.add(log);
            }
        }
        finishing.setLogs(logs);
        finishing.setHasil(listHasil);
        return finishingDb.save(finishing);
    }

}
