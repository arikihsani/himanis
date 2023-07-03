package hijrahfood.himanis.service;

import hijrahfood.himanis.model.UserModel;

import java.util.List;

public interface UserService {
    UserModel addUser(UserModel user);
    UserModel findUserByEmail(String email);
    List<UserModel> viewAllUser();
    UserModel updatePassword(UserModel user, String password);
    boolean validasiPassword(UserModel user, String password);
    String encrypt(String password);
}
