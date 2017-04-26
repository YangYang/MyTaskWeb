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
public class AuthorityFilter implements ActionFilter{

    @Inject
    Dao dao;

    private User user = null;

    @Override
    public View match(ActionContext actionContext) {
        boolean checkFlag = true;

        String sk = actionContext.getRequest().getParameter("sk");
        String ts = actionContext.getRequest().getParameter("ts");
        String ak = actionContext.getRequest().getParameter("ak");

        //验证sk
        if (!checkSecretKey(sk,ts)){
            checkFlag = false;
        }

        if (checkFlag){
            if (checkAccessKey(actionContext,ak)){
                long timeStamp = 0;
                try{
                    timeStamp = Long.parseLong(ts);
                }catch (Exception e){
                }
                if (dao.fetch(User.class,Cnd.where("username","=",user.getUsername()).and("ts","<",timeStamp)) == null){
                    checkFlag = false;
                }else {
                    user.setTs(timeStamp);
                    dao.update(user);
                }
            }else {
                checkFlag = false;
            }
        }

        if(checkFlag){
            return null;
        } else {
            Map<String, Object>result = new HashMap<>();
            result.put("code",-3);
            result.put("msg",new ConfigReader().read("-3"));
            result.put("data",new HashMap<>());
            return new ViewWrapper(new UTF8JsonView(new JsonFormat(true)),result);
        }
    }

    /**
     * 验证用户的ak
     * 验证成功写入request域，并返回true
     * 否则直接返回false
     * */
    private boolean checkAccessKey(ActionContext actionContext,String ak){
        if(ak!=null){
            User user = dao.fetch(User.class, Cnd.where("ak","=",ak));
            if(user != null){
                actionContext.getRequest().setAttribute("user",user);
                this.user = user;
                return true;
            }
        }
        return false;
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

}
