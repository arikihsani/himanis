package hijrahfood.himanis.service;

import hijrahfood.himanis.model.RoleModel;
import hijrahfood.himanis.repository.RoleDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleDb roleDb;

    @Override
    public List<RoleModel> getListRole() {
        return roleDb.findAll();
    }
}
