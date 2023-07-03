package hijrahfood.himanis.controller;

import hijrahfood.himanis.model.RoleModel;
import hijrahfood.himanis.model.UserModel;
import hijrahfood.himanis.service.RoleService;
import hijrahfood.himanis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/create")
    private String createUserFormPage(Model model) {
        UserModel user = new UserModel();
        List<RoleModel> listRole = roleService.getListRole();
        model.addAttribute("user", user);
        model.addAttribute("listRole", listRole);
        return "form-create-user";
    }

    @PostMapping(value="/create")
    private String createUserSubmit(@ModelAttribute UserModel user,
                                    @RequestParam("password") String password,
                                    @RequestParam("konfirmasiPassword") String konfirmasiPassword,
                                    RedirectAttributes red,
                                    Model model) {

        if(!password.equals(konfirmasiPassword)){
            red.addFlashAttribute("pesanError", "Password yang dimasukkan tidak sama dengan konfirmasi password!");
            return "redirect:/user/create";
        }

        if(userService.addUser(user) == null) {
            red.addFlashAttribute("pesanError", "Email sudah pernah didaftarkan! Harap gunakan email yang lain.");
            return "redirect:/user/create";
        }

        userService.addUser(user);
        red.addFlashAttribute("pesanSuccess", "Akun berhasil ditambahkan!");
        model.addAttribute("user", user);
        return "redirect:/user/create";
    }

    @GetMapping(value="/viewall")
    private String viewAllUser(Model model) {
        model.addAttribute("listUser", userService.viewAllUser());
        return "viewall-user";
    }

    @GetMapping("/changePassword")
    public String changePasswordUserFormPage(){
        return "form-change-password";
    }

    @PostMapping("/changePassword")
    @PreAuthorize("isAuthenticated()")
    public String changePasswordSubmit(Authentication auth,
                                       @RequestParam("passwordLama") String passwordLama,
                                       @RequestParam("passwordBaru") String passwordBaru,
                                       @RequestParam("konfirmasiPassword") String konfirmasiPassword,
                                       RedirectAttributes red){

        UserModel user = userService.findUserByEmail(auth.getName());
        if(!userService.validasiPassword(user, passwordLama)){
            red.addFlashAttribute("pesanError","Password lama salah! Harap memasukkan password yang benar.");
            return "redirect:/user/changePassword";
        }

        if(!passwordBaru.equals(konfirmasiPassword)){
            red.addFlashAttribute("pesanError", "Password baru tidak sesuai dengan konfirmasi password!");
            return "redirect:/user/changePassword";
        }

        userService.updatePassword(user, konfirmasiPassword);
        red.addFlashAttribute("pesanSuccess", "Password berhasil diubah!");
        return "redirect:/user/changePassword";
    }

}
