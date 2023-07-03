package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hijrahfood.himanis.model.FinishingModel;
import hijrahfood.himanis.model.ForecastingBahanModel;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastingBahanDb extends JpaRepository<ForecastingBahanModel, Long> {
    Optional<ForecastingBahanModel> findById(Long id);
    
}
