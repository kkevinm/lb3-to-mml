package com.github.kevinm.lb3tomml.lb3;

import java.util.Arrays;

import com.github.kevinm.lb3tomml.spc.Aram;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;

public class Lb3Disassembler {

    private static final int IDENTIFIER_ADDRESS = 0x0974;
    private static final int IDENTIFIER_LENGTH = 16;
    private static final int[] IDENTIFIER_DATA = {
            0xf0, 0x09, 0xf0, 0x09, 0xf0, 0x09, 0xf0, 0x09,
            0xf0, 0x09, 0xf0, 0x09, 0xf0, 0x09, 0xf0, 0x09
    };
    
    private static final int VAR_RAM_ADDRESS = 0x0000;
    private static final int MUSIC_ENGINE_ADDRESS = 0x0500;
    private static final int SAMPLE_POINTERS_ADDRESS = 0x3000;
    private static final int SONG_DATA_ADDRESS = 0xf800;

    private static final int CHANNEL_NUM = 8;

    private final Spc spc;
    private final Aram aram;
    private final SongChannel[] channels = new SongChannel[CHANNEL_NUM];

    public Lb3Disassembler(Spc spc) throws SpcException {
        this.spc = spc;
        this.aram = Aram.fromSpc(spc);

        // Check if it's actually a Last Bible III spc (with a very primitive method).
        if (!Arrays.equals(aram.getUnsignedBytes(IDENTIFIER_ADDRESS, IDENTIFIER_LENGTH), IDENTIFIER_DATA)) {
            throw new SpcException("The SPC file is not from Last Bible III!");
        }
    }

    public void disassemble() {
        // Create and process all the channels.
        for (int i = 0; i < CHANNEL_NUM; i++) {
            int channelId = aram.getUnsignedByte(SONG_DATA_ADDRESS + 3*i);
            int channelAddress = aram.getUnsignedWord(SONG_DATA_ADDRESS + 3*i + 1);
            System.out.println(String.format("Processing channel %d at address 0x%04x", channelId, channelAddress));
            SongChannel channel = new SongChannel(aram, channelId, channelAddress);
            channels[i] = channel;
            channel.disassemble();
        }
    }

}
