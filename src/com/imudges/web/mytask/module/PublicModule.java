package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.ConfigReader;
import com.imudges.web.mytask.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@IocBean
@Filters(@By(type = AuthorityFilter.class ,args={"ioc:authorityFilter"}))
public class PublicModule {
    @Inject
    Dao dao;

    @Filters(@By(type = LoginFilter.class ,args={"ioc:loginFilter"}))
    @At("public/login")
    @Ok("json")
    @Fail("http:500")
    public Object login(@Param("username") String username,
                        @Param("password") String password) {
        Map<String, Object> result = new HashMap<>();
        boolean loginFlag = false;
        if(username == null || password == null ){
            result.put("code",-1);
            result.put("msg","fail");
        } else {
            User user = dao.fetch(User.class,Cnd.where("username","=",username));
            if (user == null){
                result.put("code",-2);
            }else{
                String passwd = Toolkit.passwordEncode(password,user.getSalt());
                User test = dao.fetch(User.class, Cnd.where("username","=",user.getUsername()).and("password","=",passwd));
                if(test == null){
                    result.put("code",-2);
                }else {
                    result.put("code",0);
                    loginFlag = true;
                }
            }
        }
        result.put("msg",new ConfigReader().read(result.get("code").toString()));

        if (loginFlag){
            Map<String,Object> data = new HashMap<>();
            String ak = Toolkit.getAccessKey();
            data.put("ak",ak);
            User user = dao.fetch(User.class,Cnd.where("username","=",username));
            user.setAk(ak);
            dao.update(user);
            result.put("data",data);
        } else {
            Map<String,Object> data = new HashMap<>();
            result.put("data",data);
        }
        return result;
    }

    @At("public/get_user_info")
    @Ok("json")
    @Fail("http:500")
    public Object getUserInfo(HttpServletRequest request){
        User user = (User) request.getAttribute("user");
        Map<String,Object>data = new HashMap<>();
        data.put("user", user);
        return getSuccessResult(data);
    }

    private Map<String,Object>getSuccessResult(Map<String,Object>data){
        Map<String,Object>result = new HashMap<>();
        result.put("code","0");
        result.put("msg","ok");
        result.put("data",data);
        return result;
    }


    private Map<String,Object>getFailResult(int code,Map<String,Object>data){
        Map<String,Object>result = new HashMap<>();
        result.put("code","" + code);
        result.put("msg",new ConfigReader().read("" + code));
        result.put("data",data);
        return result;
    }

}