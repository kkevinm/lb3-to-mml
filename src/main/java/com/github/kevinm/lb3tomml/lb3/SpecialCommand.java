package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;

public class SpecialCommand extends HexCommand {

    protected SpecialCommand(int value) {
        super(value);
        
        if (value < 0xc0 || value > 0xff) {
            throw new IllegalArgumentException(String.format("Invalid special command: 0x%02x", value));
        }
    }

    @Override
    public MmlCommand process(SongChannel channel) {
        int par1;
        int par2;
        
        switch (value) {
            case 0xc0:
                // TODO
                break;
            case 0xc1:
                // TODO
                break;
            case 0xcf:
                int adsr1 = 0xff;
                int adsr2 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xd0:
                // TODO
                break;
            case 0xd1:
                // TODO
                break;
            case 0xd2:
            case 0xe0:
                // e0 also sets the tempo in a different variable...
                int tempo = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xd6:
                par1 = channel.getNextUnsignedWord();
                channel.routineCalls[0].subCall(par1);
                break;
            case 0xd7:
                channel.routineCalls[0].subReturn();
                break;
            case 0xd8:
                channel.superLoops[0].skipLast();
                break;
            case 0xd9:
                channel.superLoops[1].skipLast();
                break;
            case 0xda:
            case 0xec:
                par1 = channel.getNextUnsignedWord();
                // TODO
                break;
            case 0xe2:
                // TODO
                break;
            case 0xe3:
                int bendSpeed = channel.getNextSignedByte();
                // TODO
                break;
            case 0xe4:
                par1 = channel.getNextUnsignedByte();
                channel.superLoops[0].start(par1);
                break;
            case 0xe5:
                channel.superLoops[0].repeat();
                break;
            case 0xe6:
                par1 = channel.getNextUnsignedWord();
                channel.routineCalls[1].subCall(par1);
                break;
            case 0xe7:
                channel.routineCalls[1].subReturn();
                break;
            case 0xe8:
                par1 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xe9:
                // TODO
                break;
            case 0xea:
                // TODO
                break;
            case 0xeb:
                par1 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xed:
                channel.getNextUnsignedByte();
                break;
            case 0xee:
                // TODO
                break;
            case 0xef:
                // TODO
                break;
            case 0xf0:
                adsr1 = channel.getNextUnsignedByte();
                adsr2 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xf1:
                int sampleNumber = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xf2:
                int volume = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xf3:
                int semitoneShift = channel.getNextSignedByte();
                // TODO
                break;
            case 0xf4:
                par1 = channel.getNextUnsignedByte();
                channel.superLoops[1].start(par1);
                break;
            case 0xf5:
                channel.superLoops[1].repeat();
                break;
            case 0xf6:
                par1 = channel.getNextUnsignedWord();
                channel.routineCalls[2].subCall(par1);
                break;
            case 0xf7:
                channel.routineCalls[2].subReturn();
                break;
            case 0xf8:
                par1 = channel.getNextUnsignedWord();
                channel.unconditionalJump(par1);
                break;
            case 0xf9:
                int detune = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfa:
                par1 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfb:
                par1 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfc:
                // TODO
                break;
            case 0xfd:
                // TODO
                break;
            case 0xfe:
                par1 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xff:
            default:
                // All empty commands go here.
                break;
        }
        return MmlCommand.empty();
    }
    
}
