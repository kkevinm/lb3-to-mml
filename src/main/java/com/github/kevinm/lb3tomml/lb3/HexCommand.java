package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;

public abstract class HexCommand {
    
    protected final int value;
    
    protected HexCommand(int value) {
        this.value = value;
        
        if (value < 0 || value > 0xff) {
            throw new IllegalArgumentException(String.format("Invalid hex command: 0x%02x", value));
        }
    }
    
    public int getValue() {
        return value;
    }
    
    public abstract MmlCommand process(SongChannel channel);
    
    public static HexCommand of(int cmd) {
        if (cmd < 0x60) {
            return new NoteCommand(cmd);
        } else if (cmd < 0x80) {
            return new PanCommand(cmd);
        } else if (cmd < 0xc0) {
            return new LengthCommand(cmd);
        } else {
            return new SpecialCommand(cmd);
        }
    }
    
    @Override
    public String toString() {
        return String.format("Hex command 0x%02x", value);
    }
    
}
