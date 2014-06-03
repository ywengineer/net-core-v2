package com.handee.net.message.codec.serializable;

import com.handee.net.message.AppMessage;
import com.handee.net.message.EmptyMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageEncoder;
import com.handee.utils.ByteBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * AppMessageEncoder 应用程序的消息编码器
 */
public class SerializableMessageEncoder implements MessageEncoder {

    @Override
    public ByteBuffer encode(NetMessage msg) {
        ByteBuffer buffer = new ByteBuffer(4096);
        try {
            AppMessage appMsg = (AppMessage) msg;
            if (appMsg == null) {
                appMsg = new AppMessage(AppMessage.MESSAGE_TYPE_NULL);
                appMsg.setContent(new EmptyMessage());
            }

            int type = appMsg.getType();
            // 缓冲配置数据类型
            buffer.writeInt(type);

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ObjectOutputStream wrap = new ObjectOutputStream(out);

            if (appMsg.getContent() != null) {
                wrap.writeObject(appMsg.getContent());
            }

            // 结束

            wrap.flush();
            out.flush();
            // 序列化对象字节数组
            byte[] re = out.toByteArray();

            wrap.reset();
            out.reset();
            wrap.close();
            out.close();

            // 缓冲对象
            buffer.writeBytes(re);
            // 取出缓冲数据
            re = buffer.getBytes();
            // 清空缓冲区
            buffer.clear();

            // 缓冲大小
            buffer.writeInt(re.length);
            // 缓冲数据
            buffer.writeBytes(re);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回缓冲数据
        return buffer;
    }
}