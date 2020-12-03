package com.ssword.command.task;

import com.ssword.command.constant.Constant;
import com.ssword.command.model.Command;
import com.ssword.command.utils.CommandUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0/1 * * * * ?")
    public void consumption() {
        if (!brakeRigging()) return;
        initJdbcTemplate(jdbcTemplate);
        logger.info("{}/{}", CommandUtils.commandListSize(), CommandUtils.MAX_COMMAND_NUMBER);
        List<Command> commandList = CommandUtils.CommandPassageway(commandList());
        CommandUtils.command(commandList);
    }

    // 初始化jdbc，方便调用
    private void initJdbcTemplate(JdbcTemplate jdbcTemplate) {
        if (Constant.jdbcTemplate == null) {
            Constant.jdbcTemplate = jdbcTemplate;
        }
    }

    /**
     * 获取所有命令信息
     *
     * @return
     */
    private List<Command> commandList() {
        List<Map<String, Object>> commandList = jdbcTemplate.queryForList("SELECT * FROM t_command");
        return CommandUtils.mapToCommand(commandList);
    }

    /**
     * 制动装置
     * <p>
     * 定时器是定时执行的，有时候，我们停止了程序，但是程序停止需要一段时间，
     * 而这期间，定时器可能正好到达需要运行的点，
     * 所以在获取到关闭事件后，第一时间会改变Constant.CLOSE_FLAG参数，
     * 然后就能制止定时器里面的任务运行。
     *
     * @return
     */
    private boolean brakeRigging() {
        return Constant.CLOSE_FLAG;
    }
}
