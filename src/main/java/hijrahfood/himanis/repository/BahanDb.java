package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.BahanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface BahanDb extends JpaRepository<BahanModel, Long> {
    BahanModel getByItemIdAndProcessingId(Long itemId, Long processingId);
    @Modifying
    @Query(value = "select * from bahan " +
            "where processing_id = (:id); ", nativeQuery = true)
    @Transactional
    List<BahanModel> listBahanByProcessingId(@Param("id") Long id);

    @Modifying
    @Query(value = "DELETE from bahan " +
            "where processing_id = (:id); ", nativeQuery = true)
    @Transactional
    void deleteBahanByProcessingId(@Param("id") Long id);
}