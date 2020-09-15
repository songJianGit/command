package com.ssword.command.utils;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 生成uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            System.out.println(uuid());
        }
    }
}
