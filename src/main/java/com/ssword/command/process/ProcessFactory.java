package com.ssword.command.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessFactory {

    private static Logger logger = LoggerFactory.getLogger(ProcessFactory.class);
    private static Map<String, String> map = new ConcurrentHashMap();

    static {
        map.put("A", ProcessA.class.getName());
        map.put("B", ProcessB.class.getName());
    }

    public static Process get(String className) {
        if (className == null) {
            return null;
        }
        className = map.get(className);
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();
            return (Process) constructor.newInstance();
        } catch (Exception e) {
            logger.error("get Process error", e);
        }
        return null;
    }
}
