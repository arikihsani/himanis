package hijrahfood.himanis.service;

import hijrahfood.himanis.model.ForecastingBahanModel;

import java.util.List;

public interface ForecastingBahanService {
    ForecastingBahanModel getForecastingBahanModelById(Long id);
    List<ForecastingBahanModel> getForecastingBahanList();
    void updateForecastingBahan(ForecastingBahanModel forecastingBahan);
    void deleteForecastingBahan(ForecastingBahanModel ForecastingBahan);
}
