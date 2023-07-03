package hijrahfood.himanis.service;

import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.repository.BahanDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class BahanServiceImpl implements BahanService {
    @Autowired
    BahanDb bahanDb;

    @Override
    public List<BahanModel> getListBahan() { return bahanDb.findAll(); }

    @Override
    public List<BahanModel> getListBahanByProcessingId(Long id) {
        return bahanDb.listBahanByProcessingId(id);
    }

    @Override
    public void deleteBahanByProcessingId(Long processing_id) {
        bahanDb.deleteBahanByProcessingId(processing_id);
    }
}
