package com.github.kevinm.lb3tomml.lb3;

public class LengthCommand extends HexCommand {

    protected LengthCommand(int value) {
        super(value);
        
        if (value < 0x60 || value > 0xbf) {
            throw new IllegalArgumentException(String.format("Invalid length command: 0x%02x", value));
        }
    }
    
    public int getLength() {
        return value - 0x60;
    }

    @Override
    public void process(SongChannel channel) {
        // TODO Auto-generated method stub
        
    }
    
}
