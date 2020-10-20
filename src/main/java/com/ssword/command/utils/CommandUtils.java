package com.ssword.command.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.ssword.command.model.Command;
import com.ssword.command.model.CommandAndProcess;
import com.ssword.command.process.ProcessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class CommandUtils {

    private static final Logger logger = LoggerFactory.getLogger(CommandUtils.class);

    // 命令输出信息的线程池，队列长度100；超出队列后，拒绝并抛出异常
    private static ThreadPoolExecutor commandUtilsThreadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, Runtime.getRuntime().availableProcessors() * 4, 60, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100), Executors.defaultThreadFactory());

    public static final Integer MAX_COMMAND_NUMBER = 10;// 运行中的最大命令数
    private static Map<String, CommandAndProcess> COMMAND_LIST = new ConcurrentHashMap<>();// 正在运行的命令
    private static Map<String, Integer> COMM_TIME = new ConcurrentHashMap();// Map<命令id,秒数>

    public static List<Integer> command(List<Command> commands) {
        List<Integer> list = new ArrayList<>();
        for (Command command : commands) {
            list.add(CommandUtils.command(command));
        }
        return list;
    }

    /**
     * 记录间隔时间
     *
     * @param command
     */
    private static void commandTime(Command command) {
        if (command.getType() == 2) {
            String k = command.getId();
            if (CommandUtils.COMM_TIME.containsKey(k)) {
                if (CommandUtils.COMM_TIME.get(k) == Integer.MAX_VALUE) {
                    CommandUtils.COMM_TIME.put(command.getId(), 1);
                } else {
                    CommandUtils.COMM_TIME.put(command.getId(), CommandUtils.COMM_TIME.get(k) + 1);
                }
            } else {
                CommandUtils.COMM_TIME.put(command.getId(), 1);
            }
        } else {
            clearTime(command);
        }
    }

    // 清空间隔时间
    private static void clearTime(Command command) {
        CommandUtils.COMM_TIME.put(command.getId(), 0);
    }

    /**
     * 运行成功后，将对应的Process存入PROCESS_LIST
     *
     * @param command
     * @return
     */
    public static int command(Command command) {
        if (commandListSize() >= MAX_COMMAND_NUMBER) {
            logger.error("maximum reached:{}", MAX_COMMAND_NUMBER);
            return -1;
        }
        logger.info("commandID:{},command:[{}]", command.getId(), command.getCommand());
        try {
            process(command, 1);// 前置流程
            Process process = null;
            if (JudgeSystem.isLinux()) {
                process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command.getCommand()});
            } else {
                process = Runtime.getRuntime().exec(command.getCommand());
            }
            commandUtilsThreadPoolExecutor.execute(new StreamDrainer(command.getId(), "success", true, process.getInputStream()));
            commandUtilsThreadPoolExecutor.execute(new StreamDrainer(command.getId(), "fail", true, process.getErrorStream()));
            addCommand(new CommandAndProcess(command, process));
            process(command, 2);// 后置流程
            return 0;
        } catch (Exception e) {
            logger.error("command error");
            e.printStackTrace();
        }
        return -1;
    }

    // 根据json中的model执行不同流程
    private static void process(Command command, Integer modeltype) {
        JsonNode jsoninfo = command.getJsoninfo();
        if (jsoninfo != null) {
            JsonNode jm = jsoninfo.path("model");
            JsonNode jmt = jsoninfo.path("modeltype");
            if (jm != null && jmt != null) {
                if (jmt.asInt() == modeltype) {
                    String model = jm.asText();
                    if (model != null && model != "") {
                        com.ssword.command.process.Process ps = ProcessFactory.get(model);
                        ps.start(command);// 命令执行后的后续流程
                    }
                }
            }
        }
    }

    /**
     * map转Command
     *
     * @param map
     * @return
     */
    public static Command mapToCommand(Map<String, Object> map) {
        Command command = new Command();
        command.setId(map.get("id").toString());
        command.setCommand(map.get("command").toString());
        command.setType(Integer.valueOf(map.get("type").toString()));
        command.setIntervaltime(Integer.valueOf(map.get("intervaltime").toString()));
        command.setStatus(Integer.valueOf(map.get("status").toString()));
        Object o = map.get("port");
        if (o != null) {
            command.setPort(Integer.valueOf(o.toString()));
        } else {
            command.setPort(-1);
        }
        Object jsoninfo = map.get("jsoninfo");
        if (jsoninfo != null) {
            command.setJsoninfo(JacksonUtil.getJsonNode(jsoninfo.toString()));
        } else {
            command.setJsoninfo(null);
        }
        return command;
    }

    public static List<Command> mapToCommand(List<Map<String, Object>> maps) {
        List<Command> list = new ArrayList<>();
        for (Map m : maps) {
            list.add(mapToCommand(m));
        }
        return list;
    }

    /**
     * 添加
     *
     * @param commandAndProcess
     */
    private static void addCommand(CommandAndProcess commandAndProcess) {
        delCommand(commandAndProcess.getCommand());
        COMMAND_LIST.put(commandAndProcess.getCommand().getId(), commandAndProcess);
        logger.info("add commandID:{}", commandAndProcess.getCommand().getId());
    }

    /**
     * 检查该进程是否还存活，若还存活，则将其释放
     *
     * @param command
     */
    public static void delCommand(Command command) {
        if (COMMAND_LIST.containsKey(command.getId())) {
            Process process = COMMAND_LIST.get(command.getId()).getProcess();
            if (process.isAlive()) {// 是否存活
                process.destroy();// 释放子进程
                logger.info("destroy process commandID:{}", command.getId());
            } else {
                logger.info("destroyed process commandID:{}", command.getId());// 进程在删除前，已释放
            }
            COMMAND_LIST.remove(command.getId());
            logger.info("remove commandID:{}", command.getId());
        }
    }

    /**
     * 1.获取未被执行的命令+需要定时执行的命令
     * 2.停止被禁用的命令
     *
     * @return
     */
    public static List<Command> CommandPassageway(List<Command> list) {
        for (Command c : list) {
            commandTime(c);
        }
        if (list == null) {
            return new ArrayList<>();
        }
        List<Command> newList = new ArrayList<>();// 新命令
        for (Command command : list) {
            if (command.getStatus() == 1) {// 启用的命令
                if (CommandUtils.containsCommand(command)) {// 本命令执行过
                    if (command.getType() == 2) {// 本命令需要重复执行
                        if (COMM_TIME.get(command.getId()) >= command.getIntervaltime()) {
                            newList.add(command);
                            clearTime(command);
                        }
                    }
                } else {// 本命令未被执行
                    newList.add(command);
                }
            } else {// 禁用的命令
                CommandUtils.delCommand(command);
            }
        }
        return newList;
    }

    public static boolean containsCommand(Command command) {
        return COMMAND_LIST.containsKey(command.getId());
    }

    public static long commandListSize() {
        return COMMAND_LIST.size();
    }

    public static Map<String, CommandAndProcess> getCOMMAND_LIST() {
        return COMMAND_LIST;
    }
}
