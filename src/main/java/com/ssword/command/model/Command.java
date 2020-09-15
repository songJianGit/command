package com.ssword.command.model;

import com.fasterxml.jackson.databind.JsonNode;

public class Command {
    private String id;
    /**
     * 命令
     */
    private String command;
    /**
     * 类型 1-启动时执行一次 2-定时执行（循环任务）
     * 需要注意的是，【定时执行】的任务，【下一次的任务】执行时，如果【上一次的任务】未完成，【上一次的任务】将会被直接释放
     */
    private Integer type;
    /**
     * 定时执行命令的间隔（秒）
     */
    private Integer intervaltime;
    /**
     * 状态 1-启用 0-禁用
     */
    private Integer status;

    /**
     * 该命令所占端口（可为空）
     */
    private Integer port;

    /**
     * 该命令其他信息
     * model 流程模型
     * modeltype 1-前置（先执行流程再执行命令） 2-后置（先执行命令再执行流程）
     */
    private JsonNode jsoninfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIntervaltime() {
        return intervaltime;
    }

    public void setIntervaltime(Integer intervaltime) {
        this.intervaltime = intervaltime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public JsonNode getJsoninfo() {
        return jsoninfo;
    }

    public void setJsoninfo(JsonNode jsoninfo) {
        this.jsoninfo = jsoninfo;
    }
}
