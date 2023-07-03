package hijrahfood.himanis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hijrahfood.himanis.model.ProcessingModel;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessingDb extends JpaRepository<ProcessingModel, Long> {
    Optional<ProcessingModel> findById(Long id);
    ProcessingModel getByProcessingNumber(String processingNumber);
    ProcessingModel findTopByOrderByIdDesc();

    @Modifying
    @Query(value = "select * from processing\n" +
            "where date_created between (:awal) and (:akhir);", nativeQuery = true)
    List<ProcessingModel> filterProcessingByDate (@Param("awal") Date awal, @Param("akhir") Date akhir);


}
