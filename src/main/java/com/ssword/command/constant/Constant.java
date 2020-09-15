package com.ssword.command.constant;

import org.springframework.jdbc.core.JdbcTemplate;

public class Constant {
    /**
     * 程序终止时，防止计时器运行（true-可以运行  false-程序已终止，不可再运行）
     */
    public static boolean CLOSE_FLAG = true;
    public static JdbcTemplate jdbcTemplate = null;
}
