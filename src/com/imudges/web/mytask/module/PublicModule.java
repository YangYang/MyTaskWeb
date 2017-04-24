package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@IocBean
public class PublicModule {
    @Inject
    Dao dao;

    @At("public/login")
    @Ok("json")
    @Fail("http:500")
    public Object login(@Param("username") String username,
                        @Param("password") String password) {
        Map<String, Object> result = new HashMap<>();
        boolean loginFlag = false;
        if(username == null || username.equals("") || password == null || password.equals("")){
            result.put("code",-1);
            result.put("msg","fail");
        } else {
            User user = dao.fetch(User.class,Cnd.where("username","=",username));
            if (user == null){
                result.put("code",-3);
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
        if (loginFlag){

        }
        return result;
    }
}
