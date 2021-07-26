package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.util.Log;

public class LengthCommand extends HexCommand {

    protected LengthCommand(int value) {
        super(value);
        
        if (value < 0x80 || value > 0xbf) {
            throw new IllegalArgumentException(String.format("Invalid length command: 0x%02x", value));
        }
    }
    
    public int getLength() {
        return value - 0x7f;
    }

    @Override
    public MmlCommand process(SongChannel channel) {
        Log.log("Processing length command 0x%02x", value);
        Log.logIndent("Setting note length to %d", getLength());
        
        channel.setCurrentLength(getLength());
        return MmlCommand.empty();
    }
    
}
