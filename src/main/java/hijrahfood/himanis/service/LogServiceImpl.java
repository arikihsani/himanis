package hijrahfood.himanis.service;

import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.LogModel;
import hijrahfood.himanis.model.ProcessingModel;
import hijrahfood.himanis.repository.LogDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LogServiceImpl implements LogService {

    @Autowired
    LogDb logDb;

    @Override
    public List<LogModel> getLogsByProcessing(ProcessingModel processing) {
        return logDb.findByProcessingOrderByDateDesc(processing);
    }

    @Override
    public List<LogModel> getLogsByFinishing(FinishingModel finishing) {
        return logDb.findByFinishingOrderByDateDesc(finishing);
    }
}
