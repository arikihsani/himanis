package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hijrahfood.himanis.model.FinishingModel;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinishingDb extends JpaRepository<FinishingModel, Long> {
    Optional<FinishingModel> findById(Long id);
    FinishingModel getTopByOrderByIdDesc();

    @Modifying
    @Query(value = "select * from finishing\n" +
            "where date_created between (:awal) and (:akhir);", nativeQuery = true)
    List<FinishingModel> filterFinishingByDate (@Param("awal") Date awal, @Param("akhir") Date akhir);
}