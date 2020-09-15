package com.ssword.command.process;

import com.ssword.command.model.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessB extends ProcessBase {
    private static final Logger logger = LoggerFactory.getLogger(ProcessB.class);

    @Override
    public void _start(Command command) {
        logger.info("ProcessB");
    }
}
