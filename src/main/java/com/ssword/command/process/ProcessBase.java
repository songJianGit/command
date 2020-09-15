package com.ssword.command.process;

import com.ssword.command.model.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class ProcessBase implements Process {
    private static final Logger logger = LoggerFactory.getLogger(ProcessBase.class);

    @Override
    public void start(Command command) {
        logger.info("ProcessBase,commandID:{}", command.getId());
        _start(command);
    }

    @Override
    public void start(Command command, JdbcTemplate jdbcTemplate) {
        logger.info("ProcessBase,commandID:{}", command.getId());
        _start(command);
    }

    protected abstract void _start(Command command);// 调用子类方法
}
