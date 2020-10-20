package com.ssword.command.utils;

public class JudgeSystem {
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public static String judgeSystem() {
        if (isLinux()) {
            return "linux";
        } else if (isWindows()) {
            return "windows";
        } else {
            return "other system";
        }
    }

    public static void main(String[] args) {
        boolean flag1 = isLinux();
         System.out.println(flag1);
        boolean flag2 = isWindows();
         System.out.println(flag2);
         System.out.println(System.getProperty("os.name"));
        String sys = JudgeSystem.judgeSystem();
        System.out.println(sys);
    }
}
