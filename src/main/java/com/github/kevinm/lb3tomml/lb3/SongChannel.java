package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlMacro;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.spc.Aram;
import com.github.kevinm.lb3tomml.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SongChannel {

    private static final int MEASURE_LENGTH = 192;

    // Constants
    private final Aram aram;
    private final int id;
    private final int startAddress;

    // Runtime variables
    private int pc = 0;
    private int currentLength = 0;
    private int currentOctave = 0;
    private int currentTicks = 0;
    private boolean amplified = false;
    private boolean firstNote = true;
    private boolean legatoOn = false;
    
    final SuperLoop[] superLoops = {
            new SuperLoop(), new SuperLoop()
    };
    final RoutineCall[] routineCalls = {
            new RoutineCall(), new RoutineCall(), new RoutineCall()
    };

    private final List<MmlCommand> commands = new ArrayList<>();
    private final Set<Integer> instruments = new TreeSet<>();
    private boolean end;

    SongChannel(Aram aram, int id, int startAddress) {
        this.aram = aram;
        this.id = id;
        this.startAddress = startAddress;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (MmlCommand command: commands) {
            String cmdString = command.toString();
            output.append(cmdString);
            if (!cmdString.contains("\n") && !cmdString.equals("")) {
                output.append(" ");
            }
        }
        return output.toString();
    }
    
    public void disassemble() {
        pc = startAddress;
        end = false;
        
        commands.add(MmlCommand.dec(MmlSymbol.CHANNEL, id));
        
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

    public int getId() {
        return id;
    }

    public Set<Integer> getInstruments() {
        return instruments;
    }

    public boolean isAmplified() {
        return amplified;
    }

    public void setAmplified(boolean amplified) {
        this.amplified = amplified;
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

    public boolean isLegatoOn() {
        return legatoOn;
    }

    public void setLegato(boolean on) {
        this.legatoOn = on;
    }

    public boolean addTicks(int ticks) {
        currentTicks += ticks;
        if (currentTicks >= MEASURE_LENGTH) {
            currentTicks = 0;
            return true;
        } else {
            return false;
        }
    }

    public void addCommand(MmlCommand command) {
        commands.add(command);
    }

    public List<MmlCommand> getCommands() {
        return commands;
    }

    public boolean isFirstNote() {
        boolean isFirstNote = firstNote;
        this.firstNote = false;
        return isFirstNote;
    }
    
    public String getTickLength() {
        return "=" + currentLength;
    }

    public int getNextByte() {
        return aram.getUnsignedByte(pc++);
    }

    public int getNextWord() {
        int next = aram.getUnsignedWord(pc);
        pc += 2;
        return next;
    }

    public int getNextLong() {
        int next = aram.getUnsignedLong(pc);
        pc += 3;
        return next;
    }

    public void end() {
        this.end = true;

        Log.logIndent("Reached end of channel %d", id);
    }

    public MmlCommand buildInstrumentCommand(int value) {
        instruments.add(value);
        return new MmlCommand(MmlMacro.INSTRUMENT, String.format("%02x", value));
    }
    
    public void unconditionalJump(int address) {
        Log.log("Processing unconditional jump command");
        Log.logIndent("Jump address: 0x%04x", address);
        
        // We need to check if the target address was already visited.
        // If yes, it means the address is a loop point.
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getAddress() == address) {
                MmlCommand intro = new MmlCommand(address, MmlSymbol.INTRO);
                commands.add(i, intro);
                end = true;
                Log.logIndent("Detected channel %d loop point at 0x%02x", id, address);
                Log.logUnindent("End of channel %d", id);
                return;
            }
        }
        
        // If it's not the loop point, jump there.
        pc = address;
    }

    private void forceNewLine() {
        SongChannel.this.addCommand(MmlCommand.newline());
        SongChannel.this.currentTicks = 0;
    }

    public final class RoutineCall {

        private int returnAddress = 0;

        public void subCall(int address) {
            returnAddress = SongChannel.this.pc;
            SongChannel.this.pc = address;
            SongChannel.this.forceNewLine();
            
            Log.log("Processing call subroutine command");
            Log.logIndent("Jump address: 0x%04x - Return address: 0x%04x", address, returnAddress);
        }

        public void subReturn() {
            SongChannel.this.pc = returnAddress;
            SongChannel.this.forceNewLine();
            
            Log.log("Processing return from subroutine command");
            Log.logIndent("Returning to address 0x%04x", returnAddress);
        }

    }

    public final class SuperLoop {

        private int startAddress = 0;
        private int endAddress = 0;
        private int counter = 0;

        public void start(int counter) {
            this.counter = counter;
            this.startAddress = SongChannel.this.pc;
            SongChannel.this.forceNewLine();

            Log.log("Processing superloop start command");
            Log.logIndent("Start address: 0x%04x - Counter: %d", startAddress, counter);
        }

        public void repeat() {
            counter--;
            endAddress = SongChannel.this.pc;
            SongChannel.this.forceNewLine();
            
            Log.log("Processing superloop repeat command");
            Log.indent();
            Log.log("End address: 0x%04x - Counter: %d", endAddress, counter);
            
            if (counter != 0) {
                SongChannel.this.pc = startAddress;
                Log.log("Counter != 0: jumping back to 0x%04x", startAddress);
            } else {
                Log.log("Counter == 0: end of superloop");
            }
            
            Log.unindent();
        }

        public void skipLast() {
            Log.log("Processing superloop end command");
            
            if (counter == 1) {
                counter = 0;
                SongChannel.this.pc = endAddress;
                SongChannel.this.forceNewLine();

                Log.logIndent("Counter is 1: jumping to 0x%04x", endAddress);
            }
        }

    }

}
