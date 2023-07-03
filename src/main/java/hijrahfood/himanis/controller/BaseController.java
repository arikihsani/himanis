package hijrahfood.himanis.controller;

import hijrahfood.himanis.model.UserModel;
import hijrahfood.himanis.repository.FinishingDb;
import hijrahfood.himanis.repository.ProcessingDb;
import hijrahfood.himanis.service.UserService;
import hijrahfood.himanis.service.WarehouseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import hijrahfood.himanis.model.ProcessingModel;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    WarehouseRestService warehouseRestService;

    @Autowired
    private ProcessingDb processingDb;

    @Autowired
    private FinishingDb finishingDb;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/")
    private String home(Model model, @RequestParam(required = false) String warehouseId, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserModel user = userService.findUserByEmail(auth.getName());
        String name = user.getName();
        model.addAttribute("role", user.getRole().getRole());
        model.addAttribute("name", name);
        List <ProcessingModel> listAll = processingDb.findAll();
        ArrayList<ProcessingModel> selectedProcessing = new ArrayList<>();
        ArrayList<ProcessingModel> selectedProcessing2 = new ArrayList<>();
        for (ProcessingModel pekerjaanPesanan: listAll){
            if (pekerjaanPesanan.getApproved()==true){
                selectedProcessing.add(pekerjaanPesanan);
            }
            else{
                selectedProcessing2.add(pekerjaanPesanan);
            }
        }
        if (warehouseId != null) {
            response.addCookie(new Cookie("warehouseId", warehouseId));
        }

        model.addAttribute("processing", selectedProcessing.size());
        model.addAttribute("finishing", finishingDb.findAll().toArray().length);
        model.addAttribute("approval", selectedProcessing2.size());
        return "home";
    }

    @GetMapping("design-system")
    private String designSystem() {
        return "design-system";
    }

    @GetMapping("/warehouses")
    public String warehouses(Model model) {
        model.addAttribute("warehouses", warehouseRestService.retrieveAllWarehouses());
        return "warehouses";
    }
}
