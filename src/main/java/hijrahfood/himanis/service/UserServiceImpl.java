package hijrahfood.himanis.service;

import hijrahfood.himanis.model.UserModel;
import hijrahfood.himanis.repository.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDb userDb;

    @Override
    public UserModel addUser(UserModel user) {
        for (UserModel us : userDb.findAll()) {
            if (us.getEmail().equals(user.getEmail())) return null;
        }
        String pass = encrypt(user.getPassword());
        user.setPassword(pass);
        return userDb.save(user);
    }

    @Override
    public UserModel findUserByEmail(String email) {
        return userDb.findByEmail(email);
    }

    @Override
    public List<UserModel> viewAllUser() {
        return userDb.findAll();
    }

    @Override
    public UserModel updatePassword(UserModel user, String password) {
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        return userDb.save(user);
    }

    @Override
    public boolean validasiPassword(UserModel user, String password) {
        return new BCryptPasswordEncoder().matches(password, user.getPassword());
    }

    @Override
    public String encrypt(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(password);
        return hashedPassword;
    }

}
