package com.github.kevinm.lb3tomml.lb3;

public class SpecialCommand extends HexCommand {

    protected SpecialCommand(int value) {
        super(value);
        
        if (value < 0xc0 || value > 0xff) {
            throw new IllegalArgumentException(String.format("Invalid special command: 0x%02x", value));
        }
    }

    @Override
    public void process(SongChannel channel) {
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
                int jumpAddress = channel.getNextUnsignedWord();
                channel.returnAddress[0] = channel.pc;
                channel.pc = jumpAddress;
                break;
            case 0xd7:
                channel.pc = channel.returnAddress[0];
                break;
            case 0xd8:
                if (channel.superloopCounter[0] == 1) {
                    channel.superloopCounter[0] = 0;
                    channel.pc = channel.superloopEndAddress[0];
                }
                break;
            case 0xd9:
                if (channel.superloopCounter[1] == 1) {
                    channel.superloopCounter[1] = 0;
                    channel.pc = channel.superloopEndAddress[1];
                }
                break;
            case 0xda:
            case 0xec:
                int something6 = channel.getNextUnsignedWord();
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
                int loopCount = channel.getNextUnsignedByte();
                channel.superloopStartAddress[0] = loopCount;
                break;
            case 0xe5:
                channel.superloopEndAddress[0] = channel.pc;
                if (--channel.superloopCounter[0] != 0) {
                    channel.pc = channel.superloopStartAddress[0];
                }
                break;
            case 0xe6:
                jumpAddress = channel.getNextUnsignedWord();
                channel.returnAddress[0] = channel.pc;
                channel.pc = jumpAddress;
                break;
            case 0xe7:
                channel.pc = channel.returnAddress[0];
                break;
            case 0xe8:
                int something4 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xe9:
                // TODO
                break;
            case 0xea:
                // TODO
                break;
            case 0xeb:
                int something5 = channel.getNextUnsignedByte();
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
                loopCount = channel.getNextUnsignedByte();
                channel.superloopStartAddress[1] = loopCount;
                break;
            case 0xf5:
                channel.superloopEndAddress[1] = channel.pc;
                if (--channel.superloopCounter[1] != 0) {
                    channel.pc = channel.superloopStartAddress[1];
                }
                break;
            case 0xf6:
                jumpAddress = channel.getNextUnsignedWord();
                channel.returnAddress[1] = channel.pc;
                channel.pc = jumpAddress;
                break;
            case 0xf7:
                channel.pc = channel.returnAddress[1];
                break;
            case 0xf8:
                jumpAddress = channel.getNextUnsignedWord();
                channel.pc = jumpAddress;
                break;
            case 0xf9:
                int detune = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfa:
                int something = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfb:
                int something2 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xfc:
                // TODO
                break;
            case 0xfd:
                // TODO
                break;
            case 0xfe:
                int something3 = channel.getNextUnsignedByte();
                // TODO
                break;
            case 0xff:
            default:
                // All empty commands go here.
                break;
        }
    }
    
}
