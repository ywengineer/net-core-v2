package com.handee.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class DataWriter extends FilterOutputStream {
    private byte[] data = new byte[2];
    private OutputStream out;

    public DataWriter(OutputStream out) {
        super(out);
        this.out = out;
    }

    public void writeBoolean(boolean b) throws IOException {
        write(b ? 1 : 0);
    }

    public void writeByte(int b) throws IOException {
        write(b & 0xff);
    }

    public void writeShort(int sh) throws IOException {
        data[0] = (byte) (sh & 0xFF);
        data[1] = (byte) ((sh >> 8) & 0xff);
        write(data, 0, 2);
    }

    public void writeChar(char ch) throws IOException {
        writeShort(ch);
    }

    public void writeInt(int i) throws IOException {
        writeShort(i & 0xFFFF);
        writeShort((i >> 16) & 0xFFFF);
    }

    public void writeString(String str) throws IOException {
        if (str == null || str.length() == 0) {
            writeShort(0);
        } else {
            byte[] data = str.getBytes();
            if (data.length > 65535)
                throw new IOException("string length overflow");
            writeShort(data.length);
            write(data);
        }
    }

    public void writeShortString(String str) throws IOException {
        if (str == null || str.length() == 0) {
            writeByte(0);
        } else {
            byte[] data = str.getBytes();
            if (data.length > 255)
                throw new IOException("string length overflow");
            writeByte(data.length);
            write(data);
        }
    }

    public void write(byte[] data) throws IOException {
        out.write(data);
    }

    public void write(byte[] data, int offset, int count) throws IOException {
        out.write(data, offset, count);
    }

    public void closeStream() throws IOException {
        out.close();
    }
}