package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.spc.Aram;

public class SongChannel {
    
    // Constants
    private final Aram aram;
    private final int id;
    private final int startAddress;
    
    // Runtime variables
    private int pc;
    private int currentLength;
    private int returnAddress1;
    private int returnAddress2;
    private int superloopAddress1;
    private int superloopAddress2;
    private int superloopCounter1;
    private int superloopCounter2;
    
    // private List<>
    
    SongChannel(Aram aram, int id, int startAddress) {
        this.aram = aram;
        this.id = id;
        this.startAddress = startAddress;
        this.pc = startAddress;
    }
    
    private void disassemble() {
        boolean end = false;
        
        while (!end) { // NOSONAR
            // Read the current hex command.
            int cmd = aram.getUnsignedByte(pc++);
            decode(cmd);
        }
    }
    
    private void decode(int cmd) {
        if (cmd < 0x60) {
            
        }
    }
    
}
