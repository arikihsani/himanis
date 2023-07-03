package hijrahfood.himanis.service;

import hijrahfood.himanis.model.BahanModel;

import java.util.List;

public interface BahanService {
    List<BahanModel> getListBahan();
    List<BahanModel> getListBahanByProcessingId(Long id);
    void deleteBahanByProcessingId(Long processing_id);
}
