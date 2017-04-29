package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.Task;
import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.Config;
import com.imudges.web.mytask.util.ConfigReader;
import com.imudges.web.mytask.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@Filters(@By(type = AccessKeyFilter.class ,args={"ioc:accessKeyFilter"}))
//@Filters({@By(type = AccessKeyFilter.class, args = {"ioc:accessKeyFilter"}),@By(type = SecretKeyFilter.class, args = {"ioc:secretKeyFilter"}) } )
public class PublicModule {
    @Inject
    Dao dao;

    @Filters//(@By(type = SecretKeyFilter.class ,args={"ioc:secretKeyFilter"}))
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
            password = Toolkit._3DES_decode(Config.PASSWORD_KEY.getBytes(),Toolkit.hexstr2bytearray(password));

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
    @Ok("json:{locked:'^password$|^salt$'}")
    @Fail("http:500")
    public Object getUserInfo(HttpServletRequest request){
        User user = (User) request.getAttribute("user");
        Map<String,Object>data = new HashMap<>();
        data.put("user", user);
        return Toolkit.getSuccessResult(data);
    }

    @At("public/get_task_info")
    @Ok("json")
    @Fail("http:500")
    public Object getTaskInfo(HttpServletRequest request){
        String ak = request.getParameter("ak");

        User user = (User) request.getAttribute("user");
        if(user == null){
            return Toolkit.getFailResult(-3,null);
        }
        List<Task> tasks = null;
        tasks = dao.query(Task.class,Cnd.where("userId","=",user.getId()));
        Map<String,Object> data = new HashMap<>();
        data.put("tasks",tasks);
        return Toolkit.getSuccessResult(data);
    }

}