package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.LogModel;
import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogDb extends JpaRepository<LogModel, Long> {
    List<LogModel> findByProcessingOrderByDateDesc(ProcessingModel processing);
    List<LogModel> findByFinishingOrderByDateDesc(FinishingModel finishing);

}
