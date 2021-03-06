package com.github.kevinm.lb3tomml.lb3;

import com.github.kevinm.lb3tomml.mml.MmlMacro;
import com.github.kevinm.lb3tomml.mml.MmlSymbol;
import com.github.kevinm.lb3tomml.spc.Aram;
import com.github.kevinm.lb3tomml.spc.BrrSample;
import com.github.kevinm.lb3tomml.spc.Spc;
import com.github.kevinm.lb3tomml.spc.SpcException;
import com.github.kevinm.lb3tomml.util.Log;

import java.util.*;

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

    public static int SONG_DATA_ADDRESS_OVERRIDE = 0;

    private final Spc spc;
    private final Aram aram;
    private final SongChannel[] channels = new SongChannel[CHANNEL_NUM];
    private final Set<Integer> instruments = new TreeSet<>();
    private final List<BrrSample> samples = new ArrayList<>();

    public Lb3Disassembler(Spc spc) throws SpcException {
        this.spc = spc;
        this.aram = Aram.fromSpc(spc);

        // Check if it's actually a Last Bible III spc (with a very primitive method).
        if (!Arrays.equals(aram.getUnsignedBytes(IDENTIFIER_ADDRESS, IDENTIFIER_LENGTH), IDENTIFIER_DATA)) {
            throw new SpcException("The SPC file is not from Last Bible III!");
        }
    }

    public List<BrrSample> getSamples() {
        return samples;
    }

    public String getSamplesPath() {
        return "lb3-" + spc.id666Tags.songTitle
                .trim()
                .replaceAll("^\\d+", "")
                .replaceAll("\\s", "-")
                .toLowerCase();
    }

    public String disassemble() {
        instruments.clear();

        int songAddress = (SONG_DATA_ADDRESS_OVERRIDE == 0) ? SONG_DATA_ADDRESS : SONG_DATA_ADDRESS_OVERRIDE;

        // Create and process all the channels.
        for (int i = 0; i < CHANNEL_NUM; i++) {
            int channelId = aram.getUnsignedByte(songAddress + 3*i);
            int channelAddress = aram.getUnsignedWord(songAddress + 3*i + 1);
            
            Log.log("Processing channel %d at address 0x%04x", channelId, channelAddress);
            Log.indent();
            
            SongChannel channel = new SongChannel(aram, channelId, channelAddress);
            channels[i] = channel;
            channel.disassemble();
            instruments.addAll(channel.getInstruments());
            
            Log.unindent();
            Log.newLine();
        }

        extractSamples();

        return convert();
    }

    private void extractSamples() {
        for (int instrument: instruments) {
            int startAddress = aram.getUnsignedWord(SAMPLE_POINTERS_ADDRESS + instrument*4);
            int loopAddress = aram.getUnsignedWord(SAMPLE_POINTERS_ADDRESS + instrument*4 + 2);
            BrrSample sample = BrrSample.extract(instrument, aram, startAddress, loopAddress);
            samples.add(sample);

            Log.log("Exported sample 0x%02x", instrument);
            Log.indent();
            Log.log("Size: 0x%04x", sample.getSize());
            Log.log("Start address: 0x%04x", startAddress);
            Log.log("Loop address: 0x%04x", loopAddress);
            Log.log("AMK header: 0x%04x", sample.getAmkHeader());
            Log.unindent();
        }
    }

    private String convert() {
        StringBuilder output = new StringBuilder();
        output.append("#amk 2\n\n#spc\n{\n    #title   \"");
        output.append(spc.id666Tags.songTitle);
        output.append("\"\n    #game    \"");
        output.append(spc.id666Tags.gameTitle);
        output.append("\"\n    #author  \"");
        output.append(spc.id666Tags.artistName);
        output.append("\"\n    #comment \"\"\n}\n\n#path \"");
        output.append(getSamplesPath());
        output.append("\"\n\n#samples\n{\n    #default");
        for (BrrSample sample: samples) {
            output.append(String.format("%n    \"%s\"", sample.getFullName()));
        }
        output.append("\n}\n\n#instruments\n{");
        int instr = 30;
        for (BrrSample sample: samples) {
            output.append(String.format("%n    \"%s\" $00 $00 $9f $00 $00 ; ", sample.getFullName()));
            output.append(MmlSymbol.INSTRUMENT);
            output.append(instr++);
        }

        output.append("\n}\n\n;==================;\n; Volume  /  Tempo ;\n;==================;\n   ");
        output.append(MmlSymbol.GLOBAL_VOLUME);
        output.append("255       \n\n");

        output.append(";==================;\n;      Macros      ;\n;==================;\n");
        instr = 30;
        for (int sampleNum: instruments) {
            output.append(String.format("\"%s%02x = %s%d\"%n",
                    MmlMacro.INSTRUMENT, sampleNum, MmlSymbol.INSTRUMENT, instr++));
        }

        output.append("\n");
        output.append(MmlSymbol.ECHO_OFF);
        output.append(" ");
        output.append(MmlSymbol.LIGHT_STACCATO);
        output.append("\n");

        for (SongChannel channel: channels) {
            output.append("\n;==================;\n");
            output.append(String.format(";    Channel #%d    ;%n", channel.getId()));
            output.append(";==================;\n");
            output.append(channel.toString());
            output.append("\n");
        }

        return output.toString();
    }

}
