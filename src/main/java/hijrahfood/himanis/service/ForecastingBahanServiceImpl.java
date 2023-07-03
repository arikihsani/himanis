package hijrahfood.himanis.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import hijrahfood.himanis.model.ForecastingBahanModel;
import hijrahfood.himanis.repository.ForecastingBahanDb;

@Service
@Transactional
public class ForecastingBahanServiceImpl implements ForecastingBahanService {
    @Autowired
    ForecastingBahanDb forecastingBahanDb;

    @Override
    public void updateForecastingBahan(ForecastingBahanModel forecastingBahan){
        forecastingBahanDb.save(forecastingBahan);
    }

    @Override
    public ForecastingBahanModel getForecastingBahanModelById(Long Id){
        Optional<ForecastingBahanModel> forecastingBahan = forecastingBahanDb.findById(Id);
        if(forecastingBahan.isPresent()){
            return forecastingBahan.get();
        }
        return null;
    }

    @Override
    public List<ForecastingBahanModel> getForecastingBahanList(){
        return forecastingBahanDb.findAll();
    }

    @Override
    public void deleteForecastingBahan(ForecastingBahanModel forecastingBahanModel){
        forecastingBahanDb.delete(forecastingBahanModel);
    }
}
