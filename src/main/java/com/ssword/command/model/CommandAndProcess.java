package com.ssword.command.model;

public class CommandAndProcess {
    private Command command;// 命令对象
    private Process process;// 命令进程

    public CommandAndProcess(Command command, Process process) {
        this.command = command;
        this.process = process;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
}
