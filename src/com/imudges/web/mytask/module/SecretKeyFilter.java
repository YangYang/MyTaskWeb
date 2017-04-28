package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.ConfigReader;
import com.imudges.web.mytask.util.MD5;
import com.imudges.web.mytask.util.Toolkit;
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

/**
 * 验证SK
 */
@IocBean
public class SecretKeyFilter implements ActionFilter {

    @Inject
    private Dao dao;
    private User user = null;
    @Override
    public View match(ActionContext actionContext) {
        //获取sk ts
        String sk = actionContext.getRequest().getParameter("sk");
        String ts = actionContext.getRequest().getParameter("ts");
        //获取User对象
        this.user = (User) actionContext.getRequest().getAttribute("user");
        if(sk == null || ts == null){
            return new ViewWrapper(new UTF8JsonView(new JsonFormat(true)), Toolkit.getFailResult(-4,null));
        }

        //验证sk
        if(!checkSecretKey(sk,ts)){
            return new ViewWrapper(new UTF8JsonView(new JsonFormat(true)), Toolkit.getFailResult(-3,null));
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
            if(user!=null){
                //检测重放
                long timeStamp = 0;
                try {
                    timeStamp = Long.parseLong(ts);
                } catch (Exception e){}
                if (dao.fetch(User.class, Cnd.where("username", "=", user.getUsername()).and("ts", "<", timeStamp)) == null) {
                    return false;
                }
                //更新ts
                user.setTs(timeStamp);
                dao.update(user);
            }
            return true;
        }
        return false;
    }
}
