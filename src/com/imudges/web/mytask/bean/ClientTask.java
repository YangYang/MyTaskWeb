package com.imudges.web.mytask.bean;

import java.util.Date;

/**
 * 客户端的task pojo
 */

public class ClientTask {

    public ClientTask() {
    }

    public ClientTask(Task task) {
        this.taskName = task.getTaskName();
        this.userId = task.getUserId();
        this.summary = task.getSummary();
        this.addTime = task.getAddTime();
        this.status = task.getStatus();
        this.type = task.getType();
        this.taskWebId = task.getId() + "";
        this.syncStatus = "0";
    }

    private int id;

    private String taskName;

    private String userId;

    private String summary;

    private Date addTime;

    /**
     * 1：未完成
     * 0：完成
     * -1:放弃
     * */
    private int status;

    /**
     * 任务类型
     * 0，1，2，3四个级别
     * */
    private int type;

    private String taskWebId;

    private String syncStatus;

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getTaskWebId() {
        return taskWebId;
    }

    public void setTaskWebId(String taskWebId) {
        this.taskWebId = taskWebId;
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
