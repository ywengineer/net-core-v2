package com.handee.net.message.codec.serializable;

import com.handee.net.message.AppMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.utils.ByteBuffer;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;


/**
 * AppMessageDecoder 应用程序的消息解码器
 */
public class SerializableMessageDecoder implements MessageDecoder {

    @Override
    public NetMessage decode(ByteBuffer buffer) {
        try {

            if (buffer.available() < 6) {
                return null;
            }

            int position = buffer.position();
            int length = buffer.readInt();

            if (buffer.available() < length) {
                buffer.position(position);
                return null;
            }

            // 数据类型
            int type = buffer.readInt();

            ByteArrayInputStream in = new ByteArrayInputStream(buffer.readBytes(length - 4));

            ObjectInputStream wrap = new ObjectInputStream(in);

            Object serializeData = wrap.readObject();

            AppMessage message = new AppMessage(type);
            message.setContent(serializeData);

            return message;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public NetMessage decode(NetMessage message, ByteBuffer buffer) {

        return null;
    }
}