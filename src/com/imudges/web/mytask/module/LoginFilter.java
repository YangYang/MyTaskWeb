package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.ConfigReader;
import com.imudges.web.mytask.util.MD5;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.mvc.view.ViewWrapper;

import java.util.HashMap;
import java.util.Map;

@IocBean
public class LoginFilter implements ActionFilter {
    @Inject
    Dao dao;
    @Override
    public View match(ActionContext actionContext) {
        String sk = actionContext.getRequest().getParameter("sk");
        String ts = actionContext.getRequest().getParameter("ts");
        String username = actionContext.getRequest().getParameter("username");
        User user = null;
        if (username != null){
            user = dao.fetch(User.class, Cnd.where("username","=",username));
        }
        if(!checkSecretKey(sk,ts)){
            return new ViewWrapper(new UTF8JsonView(new JsonFormat(true)),getFailResult(-4));
        }else {
            //TODO 验证ts
            if(dao.fetch(User.class, Cnd.where("username","=",user.getUsername()).and("ts",">=",ts))!=null){
               return new ViewWrapper(new UTF8JsonView(new JsonFormat(true)),getFailResult(-4));
            }
        }
        if (user != null){
            user.setTs(System.currentTimeMillis());
            dao.update(user);
        }
        return null;
    }

    /**
     * 验证用户的sk
     * */
    private boolean checkSecretKey(String sk,String ts){
        if (sk == null || ts == null){
            return false;
        }
        if (MD5.encryptTimeStamp(ts).equals(sk)){
            return true;
        }
        return false;
    }

    private Map<String,Object>getFailResult(int code){
        Map<String, Object> result = new HashMap<>();
        result.put("code",code);
        result.put("msg",new ConfigReader().read(code + ""));
        result.put("data",new HashMap<>());
        return result;
    }
}
