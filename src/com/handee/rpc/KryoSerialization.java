
package com.handee.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInputStream;
import com.esotericsoftware.kryo.io.ByteBufferOutputStream;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.handee.rpc.FrameworkMessage.*;

import java.nio.ByteBuffer;

public class KryoSerialization implements Serialization {
    private final Kryo kryo;
    private final Input input;
    private final Output output;
    private final ByteBufferInputStream byteBufferInputStream = new ByteBufferInputStream();
    private final ByteBufferOutputStream byteBufferOutputStream = new ByteBufferOutputStream();

    public KryoSerialization() {
        this(new Kryo());
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);
    }

    public KryoSerialization(Kryo kryo) {
        this.kryo = kryo;

        kryo.register(RegisterTCP.class);
        kryo.register(RegisterUDP.class);
        kryo.register(KeepAlive.class);
        kryo.register(DiscoverHost.class);
        kryo.register(Ping.class);

        input = new Input(byteBufferInputStream, 65535);
        output = new Output(byteBufferOutputStream, 65535);
    }

    public Kryo getKryo() {
        return kryo;
    }

    @SuppressWarnings("unchecked")
	public synchronized void write(Connection connection, ByteBuffer buffer, Object object) {
        byteBufferOutputStream.setByteBuffer(buffer);
        kryo.getContext().put("connection", connection);
        kryo.writeClassAndObject(output, object);
        output.flush();
    }

    @SuppressWarnings("unchecked")
	public synchronized Object read(Connection connection, ByteBuffer buffer) {
        byteBufferInputStream.setByteBuffer(buffer);
        input.setInputStream(byteBufferInputStream);
        kryo.getContext().put("connection", connection);
        return kryo.readClassAndObject(input);
    }

    public void writeLength(ByteBuffer buffer, int length) {
        buffer.putInt(length);
    }

    public int readLength(ByteBuffer buffer) {
        return buffer.getInt();
    }

    public int getLengthLength() {
        return 4;
    }
}
