package com.ssword.command.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    // java8的新类，线程安全
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 获取当前时间
    public static String now() {
        return dtf.format(LocalDateTime.now());
    }
}
