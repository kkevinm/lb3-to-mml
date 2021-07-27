package com.github.kevinm.lb3tomml.mml;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MmlSymbol {

    public static final List<String> NOTES = Collections.unmodifiableList(Arrays.asList(
            "c", "c+", "d", "d+", "e", "f", "f+", "g", "g+", "a", "a+", "b"
    ));
    public static final String REST = "r";
    public static final String TIE = "^";
    public static final String INSTRUMENT = "@";
    public static final String VOLUME = "v";
    public static final String PAN = "y";
    public static final String OCTAVE = "o";
    public static final String OCTAVE_UP = ">";
    public static final String OCTAVE_DOWN = "<";
    public static final String DEFAULT_LENGTH = "l";
    public static final String QUANTIZATION = "q";
    public static final String GLOBAL_VOLUME = "w";
    public static final String TEMPO = "t";
    public static final String INTRO = "/";
    public static final String CHANNEL = "#";
    public static final String NOISE = "n";

    public static final String PAN_FADE = "$dc";
    public static final String PITCH_BEND = "$dd";
    public static final String VIBRATO = "$de";
    public static final String VIBRATO_OFF = "$df";
    public static final String VOLUME_FADE = "$e8";
    public static final String GLOBAL_VOLUME_FADE = "$e1";
    public static final String TEMPO_FADE = "$e3";
    public static final String GLOBAL_TRANSPOSE = "$e4";
    public static final String TREMOLO = "$e5";
    public static final String TREMOLO_OFF = "$e5$00$00$00";
    public static final String VIBRATO_FADE = "$ea";
    public static final String PITCH_ENVELOPE_TO = "$eb";
    public static final String PITCH_ENVELOPE_FROM = "$ec";
    public static final String PITCH_ENVELOPE_OFF = "$eb$00$00$00";
    public static final String ADSR = "$ed";
    public static final String GAIN = "$ed$80";
    public static final String DETUNE = "$ee";
    public static final String ECHO_SET1 = "$ef";
    public static final String ECHO_OFF = "$f0";
    public static final String ECHO_SET2 = "$f1";
    public static final String ECHO_VOLUME_FADE = "$f2";
    public static final String LEGATO_TOGGLE = "$f4$01";
    public static final String LIGHT_STACCATO = "$f4$02";
    public static final String ECHO_TOGGLE = "$f4$03";
    public static final String DISABLE_TEMPO_HIKE = "$f4$07";
    public static final String RESTORE_INSTRUMENT = "$f4$09";
    public static final String ECHO_FIR = "$f5";
    public static final String PITCH_MODULATION = "$fa$00";
    public static final String CHANNEL_TRANSPOSE = "$fa$02";
    public static final String AMPLIFY = "$fa$03";

    private MmlSymbol() {}

}
