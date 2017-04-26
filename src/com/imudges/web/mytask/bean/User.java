package com.imudges.web.mytask.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * Created by yangyang on 2017/4/23.
 */
@Table("user")
public class User {

    @Id
    private int id;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String salt;

    @Column
    private Date registerTime;

    @Column
    private String ak;

    /**
     * 0 : user
     * 1 : admin
     * */
    @Column
    private int privilege;

    //时间戳
    @Column
    private long ts;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public int getPrivilege() {
        return privilege;
    }

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }
}
