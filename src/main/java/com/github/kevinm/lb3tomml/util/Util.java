package com.github.kevinm.lb3tomml.util;

public final class Util {

    public static final String SEPARATOR = System.getProperty("file.separator");

    private Util() {}
    
    public static String hexString(int value) {
        return String.format("$%02x", value);
    }
    
    public static String decString(int value) {
        return String.format("%d", value);
    }
    
}
