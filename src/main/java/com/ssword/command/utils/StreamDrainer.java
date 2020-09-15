package com.ssword.command.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamDrainer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StreamDrainer.class);
    private InputStream ins;
    private String commandID;
    private boolean show;// 是否打印 true-是 false-否
    private String desc;// 描述说明

    public StreamDrainer(String commandID, String desc, boolean show, InputStream ins) {
        this.ins = ins;
        this.commandID = commandID;
        this.desc = desc;
        this.show = show;
    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(ins));
        try {
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (show) {
                    logger.info("commandID:{},desc:{},ConsoleMsg:{}", commandID, desc, line);
                }
            }
        } catch (Exception e) {
            logger.warn("异步日志打印进程异常！");
//            e.printStackTrace();
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
