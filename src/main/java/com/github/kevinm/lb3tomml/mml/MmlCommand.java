package com.github.kevinm.lb3tomml.mml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.kevinm.lb3tomml.util.Util;

public class MmlCommand {
    
    private int address;
    private final String command;
    private final List<String> parameters;
    
    public MmlCommand(String command, String... parameters) {
        this(0, command, parameters);
    }
    
    public MmlCommand(int address, String command, String... parameters) {
        this.address = address;
        this.command = command;
        this.parameters = new ArrayList<>(Arrays.asList(parameters));
    }
    
    public static MmlCommand empty() {
        return new MmlCommand("");
    }
    
    public static MmlCommand hex(String command, int... parameters) {
        String[] pars = Arrays.stream(parameters)
                .mapToObj(Util::hexString)
                .collect(Collectors.toList())
                .toArray(new String[] {});
        return new MmlCommand(command, pars);
    }
    
    public static MmlCommand dec(String command, int... parameters) {
        String[] pars = Arrays.stream(parameters)
                .mapToObj(Util::decString)
                .collect(Collectors.toList())
                .toArray(new String[] {});
        return new MmlCommand(command, pars);
    }
    
    public void addParameter(String parameter) {
        parameters.add(parameter);
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
