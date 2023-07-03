package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.BahanModel;
import hijrahfood.himanis.model.HasilModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface HasilDb extends JpaRepository<HasilModel, Long>{
    @Modifying
    @Query(value = "select * from hasil " +
            "where finishing_id = (:id); ", nativeQuery = true)
    @Transactional
    List<HasilModel> listHasilByFinishingId(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE from hasil " +
            "where finishing_id = (:id); ", nativeQuery = true)
    @Transactional
    void deleteHasilByFinishingId(@Param("id") Long id);
}
