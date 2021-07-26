package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlCommand;
import com.github.kevinm.lb3tomml.mml.MmlMacro;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.util.Log;
import com.github.kevinm.lb3tomml.util.Util;

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
        MmlCommand newCommand = MmlCommand.empty();
        
        switch (value) {
            case 0xc0:
                newCommand = buildInstrumentCommand(0x00);
                break;
            case 0xc1:
                newCommand = buildInstrumentCommand(0x01);
                break;
            case 0xcf:
                par1 = channel.getNextUnsignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.ADSR, 0xff-0x80, par1);
                break;
            case 0xd0:
                unsupported(value);
                break;
            case 0xd1:
                unsupported(value);
                break;
            case 0xd2:
            case 0xe0:
                par1 = channel.getNextUnsignedByte();
                int tempo = (int) Math.round(4000.0 / ((par1 + 0x80) & 0xff));
                newCommand = MmlCommand.dec(MmlSymbol.TEMPO, tempo);
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
                channel.getNextUnsignedWord();
                unsupported(value);
                break;
            case 0xe2:
                newCommand = MmlCommand.hex(MmlSymbol.PITCH_ENVELOPE_OFF);
                break;
            case 0xe3:
                channel.getNextSignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.PITCH_ENVELOPE_TO, 0x00, channel.getCurrentLength(), 0x69);
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
                channel.getNextUnsignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.VIBRATO, 0x00, 0x69, 0x69);
                break;
            case 0xe9:
                newCommand = MmlCommand.hex(MmlSymbol.VIBRATO_OFF);
                break;
            case 0xea:
                unsupported(value);
                break;
            case 0xeb:
                channel.getNextUnsignedByte();
                unsupported(value);
                break;
            case 0xed:
                channel.getNextUnsignedByte();
                unsupported(value);
                break;
            case 0xee:
                unsupported(value);
                break;
            case 0xef:
                unsupported(value);
                break;
            case 0xf0:
                par1 = channel.getNextUnsignedByte();
                par2 = channel.getNextUnsignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.ADSR, par1-0x80, par2);
                break;
            case 0xf1:
                par1 = channel.getNextUnsignedByte();
                newCommand = buildInstrumentCommand(par1);
                break;
            case 0xf2:
                par1 = channel.getNextUnsignedByte();
                newCommand = buildVolumeCommand(channel, par1);
                break;
            case 0xf3:
                par1 = channel.getNextUnsignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.CHANNEL_TRANSPOSE, par1);
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
                par1 = channel.getNextUnsignedByte();
                newCommand = MmlCommand.hex(MmlSymbol.DETUNE, par1);
                break;
            case 0xfa:
                channel.getNextUnsignedByte();
                unsupported(value);
                break;
            case 0xfb:
                channel.getNextUnsignedByte();
                unsupported(value);
                break;
            case 0xfc:
                newCommand = buildLegatoCommand(channel, true);
                break;
            case 0xfd:
                newCommand = buildLegatoCommand(channel, false);
                break;
            case 0xfe:
                channel.getNextUnsignedByte();
                unsupported(value);
                break;
            case 0xff:
            default:
                // All empty commands go here.
                break;
        }
        if (!newCommand.isEmpty()) {
            Log.logIndent("Converted to: %s", newCommand);
        }
        return newCommand;
    }
    
    private void unsupported(int command) {
        Log.logIndent("Unsupported command 0x%02x", command);
    }

    private MmlCommand buildLegatoCommand(SongChannel channel, boolean legato) {
        boolean currentLegato = channel.isLegatoOn();

        if (currentLegato != legato) {
            channel.setLegato(legato);
            return new MmlCommand(MmlSymbol.LEGATO_TOGGLE);
        } else {
            return MmlCommand.empty();
        }
    }

    private MmlCommand buildInstrumentCommand(int value) {
        return new MmlCommand(MmlMacro.INSTRUMENT, String.format("%02x", value));
    }

    private MmlCommand buildVolumeCommand(SongChannel channel, int volume) {
        int result = 0x7f - volume;
        
        if (result <= 0x4d) {
            result = (int) Math.ceil(Math.sqrt(841.53722309 * result));
            MmlCommand command = MmlCommand.dec(MmlSymbol.VOLUME, result);
            if (channel.isAmplified()) {
                channel.setAmplified(false);
                addAmplifyCommand(command, 0x00);
            }
            return command;
        } else {
            channel.setAmplified(true);
            int amp = (int) Math.ceil(256.0 * result / 0x4d - 256.0) & 0xff;
            MmlCommand command = MmlCommand.dec(MmlSymbol.VOLUME, 255);
            addAmplifyCommand(command, amp);
            return command;
        }
    }

    private void addAmplifyCommand(MmlCommand command, int amp) {
        command.addParameter(" ");
        command.addParameter(MmlSymbol.AMPLIFY);
        command.addParameter(Util.hexString(amp));
    }
    
}
