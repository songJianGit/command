package com.ssword.command.listener;

import com.ssword.command.constant.Constant;
import com.ssword.command.model.CommandAndProcess;
import com.ssword.command.utils.CommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CloseListener implements ApplicationListener<ContextClosedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(CloseListener.class);

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("捕获到程序停止事件");
        Constant.CLOSE_FLAG = false;
        closeCommandProcess();
    }

    /**
     * 关闭所有命令的进程
     */
    private static void closeCommandProcess() {
        Map<String, CommandAndProcess> map = CommandUtils.getCOMMAND_LIST();
        for (String key : map.keySet()) {
            CommandUtils.delCommand(map.get(key).getCommand());
        }
    }
}
