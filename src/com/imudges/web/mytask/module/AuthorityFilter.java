package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.ConfigReader;
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

    @Override
    public View match(ActionContext actionContext) {
        String ak = actionContext.getRequest().getParameter("ak");
        boolean checkFlag = false;
        if(ak!=null){
            User user = dao.fetch(User.class, Cnd.where("ak","=",ak));
            if(user != null){
                checkFlag = true;
                actionContext.getRequest().setAttribute("user",user);
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
}
