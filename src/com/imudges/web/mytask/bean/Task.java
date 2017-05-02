package com.imudges.web.mytask.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * Created by yangyang on 2017/4/26.
 */
@Table("task")
public class Task {
    @Id
    private int id;

    @Column
    private String taskName;

    @Column
    private String userId;

    @Column
    private String summary;

    @Column
    private Date addTime;

    /**
     * 1：未完成
     * 0：完成
     * -1：放弃
     * */
    @Column
    private int status;

    /**
     * 任务类型
     * 0，1，2，3四个级别
     * */
    @Column
    private int type;

    @Column
    private String taskWebId;

    @Column
    private String syncStatus;

    public String getTaskWebId() {
        return taskWebId;
    }

    public void setTaskWebId(String taskWebId) {
        this.taskWebId = taskWebId;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
