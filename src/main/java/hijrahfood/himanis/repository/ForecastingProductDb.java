package hijrahfood.himanis.repository;

import hijrahfood.himanis.DTO.SalesReportDTO.ProductReportDTO;
import hijrahfood.himanis.model.ForecastingProductModel;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastingProductDb extends JpaRepository<ForecastingProductModel, Long> {
    ForecastingProductModel findForecastingProductModelById(Long id);
    ForecastingProductModel findForecastingProductModelByCode(String code);
}
