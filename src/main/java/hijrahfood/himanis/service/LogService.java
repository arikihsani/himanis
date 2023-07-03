package hijrahfood.himanis.service;

import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.LogModel;
import hijrahfood.himanis.model.ProcessingModel;

import java.util.List;

public interface LogService {
    List<LogModel> getLogsByProcessing(ProcessingModel processing);
    List<LogModel> getLogsByFinishing(FinishingModel finishing);

}
