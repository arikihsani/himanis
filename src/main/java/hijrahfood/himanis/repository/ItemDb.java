package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemDb extends JpaRepository<ItemModel, Long> {
    Boolean existsByIdAndWarehouse(Long id, WarehouseModel warehouse);
    List<ItemModel> findAllByWarehouse(WarehouseModel warehouse);
    ItemModel findItemModelByCode(String code);
    Optional<ItemModel> findById(Long id);
}
