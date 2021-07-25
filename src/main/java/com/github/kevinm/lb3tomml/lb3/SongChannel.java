package com.github.kevinm.lb3tomml.lb3;

import java.util.ArrayList;
import java.util.List;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.spc.Aram;

public class SongChannel {
    
    // Constants
    final Aram aram;
    final int id;
    final int startAddress;

    // Runtime variables
    int pc = 0;
    int currentLength = 0;
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

    public void disassemble() {
        pc = startAddress;
        end = false;

        while (!end) { // NOSONAR
            int cmdAddr = pc;
            int cmd = aram.getUnsignedByte(pc++);
            System.out.println(String.format("Processing command 0x%02x on channel %d", cmd, id));
            HexCommand hexCommand = HexCommand.of(cmd);
            MmlCommand mmlCommand = hexCommand.process(this);
            if (mmlCommand == null) {
                mmlCommand = MmlCommand.empty();
            }
            mmlCommand.setAddress(cmdAddr);
            commands.add(mmlCommand);
        }
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
    
    public void unconditionalJump(int address) {
        // We need to check if the target address was already visited.
        // If yes, it means the address is a loop point.
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getAddress() == address) {
                MmlCommand intro = new MmlCommand(address, MmlSymbol.INTRO);
                commands.add(i, intro);
                end = true;
                System.out.println(String.format("Detected channel %d loop point at 0x%02x", id, address));
                return;
            }
        }
        
        // If it's not the loop point, jump there.
        if (!end) {
            pc = address;
        }
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
