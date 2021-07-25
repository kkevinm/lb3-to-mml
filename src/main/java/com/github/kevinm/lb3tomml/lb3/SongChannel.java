package com.github.kevinm.lb3tomml.lb3;

import java.util.ArrayList;
import java.util.List;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.spc.Aram;
import com.github.kevinm.lb3tomml.util.Log;

public class SongChannel {
    
    // Constants
    final Aram aram;
    final int id;
    final int startAddress;

    // Runtime variables
    private int pc = 0;
    private int currentLength = 0;
    private int currentOctave = 0;
    
    final SuperLoop[] superLoops = {
            new SuperLoop(), new SuperLoop()
    };
    final RoutineCall[] routineCalls = {
            new RoutineCall(), new RoutineCall(), new RoutineCall()
    };

    private final List<MmlCommand> commands = new ArrayList<>();
    private boolean end;

    SongChannel(Aram aram, int id, int startAddress) {
        this.aram = aram;
        this.id = id;
        this.startAddress = startAddress;
    }
    
    public int getCurrentLength() {
        return currentLength;
    }
    
    public void setCurrentLength(int currentLength) {
        this.currentLength = currentLength;
    }
    
    public int getCurrentOctave() {
        return currentOctave;
    }
    
    public void setCurrentOctave(int currentOctave) {
        this.currentOctave = currentOctave;
    }
    
    public String getTickLength() {
        return "=" + currentLength;
    }

    public int getNextSignedByte() {
        return aram.getSignedByte(pc++);
    }

    public int getNextSignedWord() {
        int next = aram.getSignedWord(pc);
        pc += 2;
        return next;
    }

    public int getNextSignedLong() {
        int next = aram.getSignedLong(pc);
        pc += 3;
        return next;
    }

    public int getNextUnsignedByte() {
        return aram.getUnsignedByte(pc++);
    }

    public int getNextUnsignedWord() {
        int next = aram.getUnsignedWord(pc);
        pc += 2;
        return next;
    }

    public int getNextUnsignedLong() {
        int next = aram.getUnsignedLong(pc);
        pc += 3;
        return next;
    }
    
    public void disassemble() {
        pc = startAddress;
        end = false;

        while (!end) {
            int cmdAddr = pc;
            int cmd = aram.getUnsignedByte(pc++);
            
            Log.log("Processing command 0x%02x in channel %d at address 0x%04x", cmd, id, cmdAddr);
            Log.indent();
            
            HexCommand hexCommand = HexCommand.of(cmd);
            MmlCommand mmlCommand = hexCommand.process(this);
            if (mmlCommand == null) {
                mmlCommand = MmlCommand.empty();
            }
            mmlCommand.setAddress(cmdAddr);
            commands.add(mmlCommand);
            
            Log.unindent();
        }
    }
    
    public void unconditionalJump(int address) {
        // We need to check if the target address was already visited.
        // If yes, it means the address is a loop point.
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getAddress() == address) {
                MmlCommand intro = new MmlCommand(address, MmlSymbol.INTRO);
                commands.add(i, intro);
                end = true;
                Log.log("Detected channel %d loop point at 0x%02x", id, address);
                return;
            }
        }
        
        // If it's not the loop point, jump there.
        pc = address;
    }

    public final class RoutineCall {

        private int returnAddress = 0;

        public void subCall(int address) {
            returnAddress = SongChannel.this.pc;
            SongChannel.this.pc = address;
        }

        public void subReturn() {
            SongChannel.this.pc = returnAddress;
        }

    }

    public final class SuperLoop {

        private int startAddress = 0;
        private int endAddress = 0;
        private int counter = 0;

        public void start(int counter) {
            this.counter = counter;
        }

        public void repeat() {
            endAddress = SongChannel.this.pc;
            if (--counter != 0) {
                SongChannel.this.pc = startAddress;
            }
        }

        public void skipLast() {
            if (counter == 1) {
                counter = 0;
                SongChannel.this.pc = endAddress;
            }
        }

    }

}
