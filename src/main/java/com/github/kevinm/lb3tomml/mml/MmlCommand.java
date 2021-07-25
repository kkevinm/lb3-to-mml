package com.github.kevinm.lb3tomml.mml;

import java.util.Arrays;

public class MmlCommand {
    
    private int address;
    private final String command;
    private final String[] parameters;
    
    public MmlCommand(String command, String... parameters) {
        this(0, command, parameters);
    }
    
    public MmlCommand(int address, String command, String... parameters) {
        this.address = address;
        this.command = command;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }
    
    public static MmlCommand empty() {
        return new MmlCommand("");
    }
    
    public boolean isEmpty() {
        return command == null || "".equals(command);
    }
    
    public int getAddress() {
        return address;
    }
    
    public void setAddress(int address) {
        this.address = address;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(command);
        for (String str: parameters) {
            result.append(str);
        }
        return result.toString();
    }
    
}
