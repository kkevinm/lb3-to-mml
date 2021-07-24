package com.github.kevinm.lb3tomml.lb3;

public abstract class HexCommand {
    
    protected final int value;
    
    protected HexCommand(int value) {
        if (value < 0 || value > 0xff) {
            throw new IllegalArgumentException(String.format("Invalid hex command: 0x%2X", value));
        }
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
}
