package com.ssword.command.process;

import com.ssword.command.model.Command;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 流程
 */
public interface Process {
    void start(Command command);
    void start(Command command, JdbcTemplate jdbcTemplate);
}
