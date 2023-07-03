package hijrahfood.himanis.service;

import hijrahfood.himanis.model.HasilModel;
import hijrahfood.himanis.repository.HasilDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class HasilServiceImpl implements HasilService {
    @Autowired
    HasilDb hasilDb;

    @Override
    public List<HasilModel> getListHasil() { return hasilDb.findAll(); }

    @Override
    public List<HasilModel> getListHasilByFinishingId(Long id) {
        return hasilDb.listHasilByFinishingId(id);
    }

    @Override
    public void deleteHasilByFinishingId(Long finishing_id) {
        hasilDb.deleteHasilByFinishingId(finishing_id);
    }
}
