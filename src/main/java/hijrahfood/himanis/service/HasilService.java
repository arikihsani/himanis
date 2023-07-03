package hijrahfood.himanis.service;

import hijrahfood.himanis.model.HasilModel;

import java.util.List;

public interface HasilService {
    List<HasilModel> getListHasil();
    List<HasilModel> getListHasilByFinishingId(Long id);
    void deleteHasilByFinishingId(Long finishing_id);
}
