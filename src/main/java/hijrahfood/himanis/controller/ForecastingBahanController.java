package hijrahfood.himanis.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import hijrahfood.himanis.model.ForecastingBahanModel;
import hijrahfood.himanis.model.ItemModel;
import hijrahfood.himanis.service.ForecastingBahanService;
import hijrahfood.himanis.service.ItemRestService;

@Controller
@RequestMapping("/forecasting")
public class ForecastingBahanController {
    @Qualifier("forecastingBahanServiceImpl")

    @Autowired
    private ForecastingBahanService forecastingService;

    @Autowired
    private ItemRestService itemRestService;

    @GetMapping("/bahan/view-all")
    private String listForecastingBahan(Model model){
        List<ItemModel> listItem = itemRestService.getAllItems();
        List<Double> totalBahan = new ArrayList<>();
        for (ItemModel item: listItem){
            if (item.getForecastingBahan().size() == 0){
                totalBahan.add(0.0);
            }
            else{
                Double total = 0.0;
                for (ForecastingBahanModel bahan: item.getForecastingBahan()){
                    total += bahan.getQuantity();
                }
                totalBahan.add(total);
            }
        }
        model.addAttribute("listItem",listItem);
        model.addAttribute("totalBahan", totalBahan);
        return "view-all-forecastingBahan";
    }

    @GetMapping("/bahan/detail")
    private String detailPekerjaanPesanan(@RequestParam(value = "id") Long id, Model model) {
        ItemModel item = itemRestService.getItemModelById(id);
        List<ForecastingBahanModel> listForecastingBahan = item.getForecastingBahan();
        List<ForecastingBahanModel> selectedList = new ArrayList<>();
        List<ItemModel> listBahan = new ArrayList<>();
        for(ForecastingBahanModel bahan: listForecastingBahan){
            if (id == bahan.getItem().getId()){
                ItemModel selected = itemRestService.getItemModelById(bahan.getReferred_by());
                listBahan.add(selected);
                selectedList.add(bahan);
            }
            
        }
        List<ItemModel> listAllItem = itemRestService.getAllItems();
        ForecastingBahanModel bahanModel = new ForecastingBahanModel();
        model.addAttribute("bahanModel", bahanModel);
        model.addAttribute("item", item);
        model.addAttribute("listForecastingBahan", selectedList);
        model.addAttribute("listBahan", listBahan);
        model.addAttribute("listItem", listAllItem);
        

        return "detail-forecastingBahan";
    }

    @PostMapping(value = "/bahan/add/{id}")
    public String updateStatusPekerjaanPesanan(@PathVariable Long id,Model model, RedirectAttributes red,
    @ModelAttribute ForecastingBahanModel FBModel) {
        
        // ini item yang baru ditambahkan
        ItemModel selected = itemRestService.getItemModelById(FBModel.getReferred_by());

        // set quantity itemnya ke forecasting bahan model
        FBModel.setQuantity(selected.getQuantity());
        
        // set item model utama
        ItemModel main = itemRestService.getItemModelById(id);
        FBModel.setItem(main);

        // save ke db
        forecastingService.updateForecastingBahan(FBModel);

        // model.addAttribute("processingNumber", processing.getProcessingNumber());
        // processingService.updateProcessing(processing);
        // red.addFlashAttribute("pesanSuccess", "Status Pekerjaan Pesanan " + "#" + idProcess + " berhasil diubah!");
        return "redirect:/forecasting/bahan/detail/?id=" + id;
    }

    @RequestMapping(value = "/bahan/delete/{id}/{idItem}", method = RequestMethod.GET)
    public String deletePekerjaanPesanan(@PathVariable Long id, @PathVariable Long idItem, Model model, RedirectAttributes red) {
        ForecastingBahanModel FBModel = forecastingService.getForecastingBahanModelById(id);
        forecastingService.deleteForecastingBahan(FBModel);
        
        return "redirect:/forecasting/bahan/detail/?id=" + idItem;
    }

}
