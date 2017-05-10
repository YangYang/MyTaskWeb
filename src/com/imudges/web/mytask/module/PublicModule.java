package com.imudges.web.mytask.module;

import com.imudges.web.mytask.bean.ClientTask;
import com.imudges.web.mytask.bean.Task;
import com.imudges.web.mytask.bean.User;
import com.imudges.web.mytask.util.Config;
import com.imudges.web.mytask.util.ConfigReader;
import com.imudges.web.mytask.util.Toolkit;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.tools.Tool;
import java.util.ArrayList;
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
            result.put("userId",user.getId()); 
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

    /**
     * 请求服务器数据库中task
     * */
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

        //将服务器的Task构造为客户端的Task
        List<ClientTask> clientTaskList = new ArrayList<>();
        for(Task t : tasks){

            //构造函数已经将webId、syncStatus修改
            ClientTask clientTask = new ClientTask(t);
            clientTaskList.add(clientTask);
        }
        Map<String,Object> data = new HashMap<>();
        //返回的数据为客户端的数据格式
        data.put("tasks",clientTaskList);
        return Toolkit.getSuccessResult(data);
    }

    @At("public/upload_task")
    @Ok("json")
    @Fail("http:500")
    public Object uploadTask(HttpServletRequest request){
        //放置返回结果的map
        Map<String,Object> result = new HashMap<>();
        String ak = request.getParameter("ak");
        String userId = dao.fetch(User.class,Cnd.where("ak","=",ak)).getId() + "";
        //无Pojo类
        String json = request.getParameter("tasks");
        List<ClientTask> clientTaskList = Json.fromJsonAsList(ClientTask.class,json);
        if(clientTaskList == null || clientTaskList.size() == 0){
            //请求参数无效
            return Toolkit.getFailResult(-4,result);
        }
        //判断数据是否需要同步
        for(ClientTask clientTask : clientTaskList){
            Task task = new Task(clientTask);
            if(clientTask.getSyncStatus() == null || clientTask.getSyncStatus().equals("null")){
                dao.insert(task);
                dao.fetch(task);
                clientTask.setTaskWebId(task.getId() + "");
                clientTask.setSyncStatus("0");
            } else if(clientTask.getSyncStatus().equals("1")){
                task.setId(Integer.parseInt(clientTask.getTaskWebId()));
                dao.update(task);
                clientTask.setSyncStatus("0");
            }
            result.put(clientTask.getId() + "",clientTask);
        }
        return Toolkit.getSuccessResult(result);
    }


    //TODO 重放问题
    @At("public/midifiy_password")
    @Ok("json")
    @Fail("http:500")
    public Object midifyPassword(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        String ak = request.getParameter("ak");
        String oldPassword = request.getParameter("old_password");
        String newPassword = request.getParameter("new_password");
        User user = (User) request.getAttribute("user");
        if(oldPassword == null || oldPassword.equals("") || newPassword == null || newPassword.equals("")){
            //请求参数无效
            return Toolkit.getFailResult(-4,result);
        }
        oldPassword = Toolkit._3DES_decode(Config.PASSWORD_KEY.getBytes(),Toolkit.hexstr2bytearray(oldPassword));
        oldPassword = Toolkit.passwordEncode(oldPassword,user.getSalt());
        String ts = (String) request.getAttribute("ts");
        if(dao.fetch(User.class,Cnd.where("ak","=",ak).and("password","=",oldPassword))!=null){
            newPassword = Toolkit._3DES_decode(Config.PASSWORD_KEY.getBytes(),Toolkit.hexstr2bytearray(newPassword));
            newPassword = Toolkit.passwordEncode(newPassword,user.getSalt());
            user.setPassword(newPassword);
            dao.update(user);
            return Toolkit.getSuccessResult(result);
        }
        //旧密码错误
        return Toolkit.getFailResult(-5,result);
    }

}