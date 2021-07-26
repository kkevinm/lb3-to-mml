package com.github.kevinm.lb3tomml.spc;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Spc {
    
    public static final int RAM_LEN = 0x10000;
    public static final int REG_LEN = 0x80;
    public static final int IPL_ROM_LEN = 0x40;
    public static final int HEADER_LEN = 33;
    public static final String SPC_HEADER = "SNES-SPC700 Sound File Data v0.30";
    
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy\0");
    
    public final byte version;
    public final short pc;
    public final byte a;
    public final byte x;
    public final byte y;
    public final byte psw;
    public final byte sp;
    public final Id666Tags id666Tags;
    public final byte[] ram;
    public final byte[] regs;
    public final byte[] iplRom;
    
    public Spc(byte version, short pc, byte a, byte x, byte y, byte psw, byte sp, Id666Tags id666Tags,
            byte[] ram, byte[] regs, byte[] iplRom) {
        if(ram.length != RAM_LEN || regs.length != REG_LEN || iplRom.length != IPL_ROM_LEN) {
            throw new IllegalArgumentException("Illegal arguments provided");
        }
        this.version = version;
        this.pc = pc;
        this.a = a;
        this.x = x;
        this.y = y;
        this.psw = psw;
        this.sp = sp;
        this.id666Tags = id666Tags;
        this.ram = ram;
        this.regs = regs;
        this.iplRom = iplRom;
    }
    
    public static Spc loadSpc(String file) throws IOException, SpcException {
        return Spc.loadSpc(new File(file));
    }
    
    public static Spc loadSpc(File file) throws IOException, SpcException {
        if(!file.getName().endsWith(".spc")) {
            throw new SpcException("The file is not an SPC!");
        }
        try(RandomAccessFile reader = new RandomAccessFile(file, "r")) {
            byte[] header = new byte[HEADER_LEN];
            reader.read(header);
            if(!SPC_HEADER.equals(new String(header))) {
                throw new SpcException("Invalid header string!");
            }
            
            if(reader.readShort() != 0x1A1A) {
                throw new SpcException("Invalid padding bytes!");
            }
            
            boolean hasId666Tag;
            switch(reader.readByte()) {
            case 0x1A:
                hasId666Tag = true;
                break;
            case 0x1B:
                hasId666Tag = false;
                break;
            default:
                throw new SpcException("Unable to determine if the file contains ID666 tag!");
            }
            
            byte version = reader.readByte();
            short pc = reader.readShort();
            byte a = reader.readByte();
            byte x = reader.readByte();
            byte y = reader.readByte();
            byte psw = reader.readByte();
            byte sp = reader.readByte();
            
            Id666Tags id666Tags = null;
            
            if(hasId666Tag) {
                reader.seek(0x2E);
                id666Tags = Id666Tags.getId666Tag(reader);
            }
            
            byte[] ram = new byte[RAM_LEN];
            byte[] regs = new byte[REG_LEN];
            byte[] iplRom = new byte[IPL_ROM_LEN];
            
            reader.seek(0x100);
            reader.read(ram);
            reader.read(regs);
            reader.seek(0x101C0);
            reader.read(iplRom);
            
            return new Spc(version, pc, a, x, y, psw,
                    sp, id666Tags, ram, regs, iplRom);
        }
    }
    
    public static class Id666Tags {
        public final String songTitle;
        public final String gameTitle;
        public final String dumperName;
        public final String comments;
        public final LocalDate dumpDate;
        public final int secondsBeforeFadeOut;
        public final int fadeOutLength;
        public final String artistName;
        public final byte defaultChannelDisables;
        public final SnesEmulator dumpEmulator;
        
        Id666Tags(String songTitle, String gameTitle, String dumperName, String comments, LocalDate dumpDate,
                int secondsBeforeFadeOut, int fadeOutLength, String artistName, byte defaultChannelDisables,
                SnesEmulator dumpEmulator) {
            this.songTitle = songTitle;
            this.gameTitle = gameTitle;
            this.dumperName = dumperName;
            this.comments = comments;
            this.dumpDate = dumpDate;
            this.secondsBeforeFadeOut = secondsBeforeFadeOut;
            this.fadeOutLength = fadeOutLength;
            this.artistName = artistName;
            this.defaultChannelDisables = defaultChannelDisables;
            this.dumpEmulator = dumpEmulator;
        }
        
        @Override
        public String toString() {
            return songTitle + "\n" + gameTitle + "\n" + artistName + "\n" + dumperName + "\n" + comments + "\n" + dumpDate.toString() + "\n";
        }
        
        /**
         * Loads the ID666 tags from the file starting from the current
         * file position pointer (for SPC files this must be 0x2E).
         * 
         * @param  file
         * @return an {@link Id666Tags}
         * @throws IOException 
         */
        static Id666Tags getId666Tag(RandomAccessFile file) throws IOException {
            String songTitle = readString(file, 32);
            String gameTitle = readString(file, 32);
            String dumperName = readString(file, 16);
            String comments = readString(file, 32);
            
            /*
             * Check if date and length tags are in text or binary form
             * (by checking if they only contain bytes that encode to digits or the '/' character).
             */
            file.seek(0x9E);
            boolean isTextFormat = isTextRegion(file, 11);
            if(isTextFormat) {
                file.seek(0xA9);
                isTextFormat = isTextRegion(file, 3);
            }
            
            LocalDate dumpDate = null;
            int secondsBeforeFadeOut;
            int fadeOutLength;
            
            file.seek(0x9E);
            if(isTextFormat) {
                String date = readString(file, 11);
                if (!"".equals(date.trim())) {
                    dumpDate = LocalDate.parse(date, dateFormat);
                }
                secondsBeforeFadeOut = readNumber(file, 3);
                fadeOutLength = readNumber(file, 5);
            } else {
                short year = file.readShort();
                byte month = file.readByte();
                byte day = file.readByte();
                dumpDate = LocalDate.of(year, month, day);
                file.seek(0xA9);
                secondsBeforeFadeOut = file.readShort();
                file.readByte();
                fadeOutLength = file.readInt();
            }
            
            String artistName = readString(file, 32);
            byte defaultChannelDisables = file.readByte();
            SnesEmulator dumpEmulator = SnesEmulator.fromId(getDigit(file.readByte()));
            
            return new Id666Tags(songTitle, gameTitle, dumperName, comments, dumpDate,
                    secondsBeforeFadeOut, fadeOutLength, artistName, defaultChannelDisables, dumpEmulator);
        }
        
        /**
         * Reads {@code maxLength} bytes from {@code file} and encodes them as a {@link String}.
         * The string will contain all the characters up to the terminator, but the position pointer
         * will be increased by {@code maxLength} regardless.
         * 
         * @param file
         * @param maxLength
         * @return
         * @throws IOException
         */
        static String readString(RandomAccessFile file, int maxLength) throws IOException {
            byte[] res = new byte[maxLength];
            boolean hasEnded = false;
            for(int i = 0; i < maxLength; i++) {
                byte b = file.readByte();
                if(!hasEnded) {
                    if(b == 0) {
                        hasEnded = true;
                    } else {
                        res[i] = b;
                    }
                }
            }
            return new String(res);
        }
        
        /**
         * Converts {@code b} to its character representation, and then returns
         * the corresponding integer (if the character is a digit).
         * 
         * @param b
         * @return
         * @throws IllegalArgumentException If the character encoded is not a digit.
         */
        static int getDigit(byte b) {
            char c = (char) (b & 0xFF);
            if(!Character.isDigit(c))
                throw new IllegalArgumentException("Expected a numeric value");
            return c;
        }
        
        /**
         * Reads up to maxLength bytes and encodes them as an integer.
         * If the terminator is found, the read stops.
         * 
         * @param file
         * @param maxLength
         * @return
         * @throws IOException
         */
        static int readNumber(RandomAccessFile file, int maxLength) throws IOException {
            int res = 0;
            for(int i = 0; i < maxLength; i++) {
                byte b = file.readByte();
                if(b == 0)
                    break;
                res *= 10;
                res += getDigit(b);
            }
            return res;
        }
        
        /**
         * Reads {@code length} bytes, and returns {@code true} if all of them,
         * encoded in ASCII, represent a digit or the '/' character.
         * This is used to check if some ID666 tags are encoded in text or binary form
         * by checking it on the date and length tags.
         * 
         * @param file
         * @param length
         * @return
         * @throws IOException
         */
        static boolean isTextRegion(RandomAccessFile file, int length) throws IOException {
            for(int i = 0; i < length; i++) {
                byte b = file.readByte();
                if(b != 0) {
                    char c = (char) (b & 0xFF);
                    if(!Character.isDigit(c) && c != '/') {
                        return false;
                    }
                }
            }
            return true;
        }
    }
    
}
