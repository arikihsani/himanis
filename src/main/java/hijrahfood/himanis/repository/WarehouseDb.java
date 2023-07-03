package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseDb extends JpaRepository<WarehouseModel, Long> {
}
