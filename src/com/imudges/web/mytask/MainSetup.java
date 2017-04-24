package com.imudges.web.mytask;

/**
 * Created by yangyang on 2017/4/23.
 */
import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import java.util.Date;

public class MainSetup implements Setup{
    public static Ioc ioc;
    @Override
    public void destroy(NutConfig arg0) {
    }

    @Override
    public void init(NutConfig conf) {
        MainSetup.ioc = conf.getIoc();
        Dao dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, "com.imudges.web.mytask", false);

        if(dao.count(User.class) == 0){
            User user = new User();
            user.setUsername("admin");
            Toolkit.generatePasswd(user,"123456");
            user.setPrivilege(1);
            user.setRegisterTime(new Date(System.currentTimeMillis()));
            dao.insert(user);
        }
    }
}