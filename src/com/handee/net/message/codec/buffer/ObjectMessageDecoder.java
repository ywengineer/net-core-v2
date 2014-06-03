package com.handee.net.message.codec.buffer;

import com.handee.net.message.NetMessage;
import com.handee.net.message.ObjectMessage;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.utils.ByteBuffer;

/**
 * 消息解码器。
 *
 * @author Mark
 */
public class ObjectMessageDecoder implements MessageDecoder {
    public NetMessage decode(ByteBuffer buffer) {
        if (buffer.available() < 6)
            return null;
        //
        int messagePosition = buffer.position();
        int length = buffer.readInt();

        int dataPosition = buffer.position();
        int type = buffer.readUnsignedShort();

        // 消息不完整
        if (buffer.available() < length) {
            buffer.position(messagePosition);
            return null;
        }

        // 空消息
        if (length < 0) {
            System.out.println("nagative length:" + length);
            System.out.println("buffer available:" + buffer.available());
            System.out.println("type:" + type);
            return null;
        }

        // 数据位
        buffer.position(dataPosition);

        ByteBuffer dataBuffer = new ByteBuffer(length);
        dataBuffer.writeByteBuffer(buffer, length);
        return new ObjectMessage(dataBuffer.getRawBytes());
    }

    public NetMessage decode(NetMessage message, ByteBuffer buffer) {

        return null;
    }
}