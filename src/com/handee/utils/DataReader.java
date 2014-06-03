package com.handee.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class DataReader extends FilterInputStream {

	public DataReader(InputStream in) {
		super(in);
	}

	public boolean readBoolean() throws IOException {
		return readByte() == 1;
	}

	public byte readByte() throws IOException {
		int b = read();
		if (b < 0)
			throw new IOException("end of inputstream");
		return (byte) b;
	}

	public byte[] readBytes(int count) throws IOException {
		byte[] data = new byte[count];
		for (int i = 0; i < count; i++)
			data[i] = readByte();
		return data;
	}

	public short readShort() throws IOException {
		int b1 = readByte() & 0xFF;
		int b2 = readByte() & 0xFF;
		return (short) (b1 | b2 << 8);
	}

	public char readChar() throws IOException {
		return (char) readShort();
	}

	public int readInt() throws IOException {
		int s1 = readShort() & 0xFFFF;
		int s2 = readShort() & 0xFFFF;
		return s1 | s2 << 16;
	}

	public String readString() throws IOException {
		int len = readShort() & 0xFFFF;
		if (len == 0)
			return null;
		byte[] data = readBytes(len);
		return new String(data);
	}

	public String readShortString() throws IOException {
		int len = readByte() & 0xFF;
		if (len == 0)
			return null;
		byte[] data = readBytes(len);
		return new String(data);
	}

	public void closeStream() throws IOException {
		in.close();
	}
}