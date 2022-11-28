package com.ssword.command.process;

import com.ssword.command.constant.Constant;
import com.ssword.command.model.Command;
import com.ssword.command.utils.DateUtils;
import com.ssword.command.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ProcessA extends ProcessBase {
    private static final Logger logger = LoggerFactory.getLogger(ProcessA.class);

    @Override
    protected void _start(Command command) {
        logger.info("ProcessA");
        savePackinfo(command);
    }

    private void savePackinfo(Command command) {
        String sql = "INSERT INTO `command`.`t_packinfo` (`id`, `cdate`, `psize`, `port`, `remarks`) VALUES (?, ?, ?, ?, ?)";
        long pSize = getFileSize(command.getJsoninfo().path("fileurl").asText());
        if (pSize != 0) {
            Constant.jdbcTemplate.update(sql, UUIDUtils.uuid(), DateUtils.now(), pSize, command.getPort(), command.getCommand());
        }
    }

    public static long getFileSize(String fileurl) {
        File file = new File(fileurl);
        if (file.exists() && file.isFile()) {
            long fs = file.length();
            logger.info("文件[{}]的大小是:{}", file.getName(), fs);
            return fs;
        } else {
            return 0l;
        }
    }
}
