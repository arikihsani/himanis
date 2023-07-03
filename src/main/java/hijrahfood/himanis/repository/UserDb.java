package hijrahfood.himanis.repository;

import hijrahfood.himanis.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDb extends JpaRepository<UserModel, Long> {
    UserModel findByEmail(String email);
}
