package hijrahfood.himanis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import hijrahfood.himanis.DTO.BahanDTO;
import hijrahfood.himanis.DTO.LinesAttributesDTO;
import hijrahfood.himanis.DTO.ProcessingDTO;
import hijrahfood.himanis.DTO.StockAdjustmentDTO;
import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.HasilModel;
import hijrahfood.himanis.model.LogModel;
import hijrahfood.himanis.model.ProcessingModel;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessingRestServiceImpl implements ProcessingRestService {

    @Autowired
    ProcessingDb processingDb;

    @Autowired
    WarehouseDb warehouseDb;

    @Autowired
    UserDb userDb;

    @Autowired
    ItemDb itemDb;

    @Autowired
    BahanDb bahanDb;

    @Autowired
    LogDb logDb;

    public ProcessingModel getProcessingById(Long id) {
        return processingDb.findById(id).get();
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public class StockAdjustmentDTOContainer {
        private StockAdjustmentDTO stock_adjustment;
    }

    @Override
    public ProcessingModel processingUpdate(Long id, ProcessingDTO processingDTO, Authentication authentication) {
        List<String> activityLog = new ArrayList<>();
        ProcessingModel processing = processingDb.getById(id);
        if (!processing.getOutputTarget().equals(processingDTO.getOutputTarget())) {
            activityLog.add("Luaran");
            processing.setOutputTarget(processingDTO.getOutputTarget());
        } else {
            processing.setOutputTarget(processingDTO.getOutputTarget());
        }

        if (!processing.getDescription().equals(processingDTO.getDescription())) {
            activityLog.add("Deskripsi");
            processing.setDescription(processingDTO.getDescription());
        } else {
            processing.setDescription(processingDTO.getDescription());
        }

        processing.setStatus(processing.getStatus());
        processing.setWarehouse(warehouseDb.getById(processingDTO.getIdWarehouse()));
        processing.setApproved(true);
        processing.setDateCreated(processing.getDateCreated());
        processing.setAssignee(processing.getAssignee());
        processing.setProcessingNumber(processing.getProcessingNumber());
        processing = processingDb.save(processing);
        List<BahanModel> listBahan = new ArrayList<>();
        List<LinesAttributesDTO> linesAttributesDTOList = new ArrayList<>();
        Map<Long, BahanModel> bahanMap = new HashMap<Long, BahanModel>();
        for (BahanModel i : processing.getListBahan()) bahanMap.put(i.getItem().getId(), i);

        for (BahanDTO bahan : processingDTO.getListBahan()) {
            if (bahanMap.containsKey(bahan.getIdItem())) {
                BahanModel bahanModel = bahanMap.get(bahan.getIdItem());

                linesAttributesDTOList.add(new LinesAttributesDTO(bahanModel.getItem().getName(), bahanModel.getQuantity() - bahan.getQuantity()));
                System.out.println(bahanModel.getItem().getName());
                System.out.println(bahan.getQuantity() - bahanModel.getQuantity());

                if (!Objects.equals(bahanModel.getQuantity(), bahan.getQuantity())) {
                    activityLog.add("Bahan");
                    bahanModel.setQuantity(bahan.getQuantity());
                } else {
                    bahanModel.setQuantity(bahan.getQuantity());
                }

                bahanModel = bahanDb.save(bahanModel);
                listBahan.add(bahanModel);
            } else {
                activityLog.add("Bahan");
                BahanModel bahanModel = new BahanModel();
                bahanModel.setItem(itemDb.getById(bahan.getIdItem()));
                bahanModel.setQuantity(bahan.getQuantity());
                bahanModel.setProcessing(processing);
                bahanModel = bahanDb.save(bahanModel);
                listBahan.add(bahanModel);

                linesAttributesDTOList.add(new LinesAttributesDTO(itemDb.getById(bahan.getIdItem()).getName(), bahan.getQuantity() * -1));
            }
        }

        StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO("production",
                processing.getWarehouse().getId().intValue(),
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
        logModel.setProcessing(processing);
        logModel.setAssignee(userDb.findByEmail(authentication.getName()).getName());
        logModel.setActivity(StringUtils.join(activityLog, ", "));
        logDb.save(logModel);

        if (processing.getLogs().isEmpty()) {
            logs.add(logModel);
        } else {
            for (LogModel log : processing.getLogs()) {
                logs.add(log);
            }
        }
        processing.setLogs(logs);

        processing.setListBahan(listBahan);
        return processingDb.save(processing);
    }
}
