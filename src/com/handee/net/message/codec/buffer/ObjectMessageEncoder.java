package com.handee.net.message.codec.buffer;

import com.handee.net.message.NetMessage;
import com.handee.net.message.ObjectMessage;
import com.handee.net.message.codec.MessageEncoder;
import com.handee.utils.ByteBuffer;

/**
 * 
 * 消息编码器。
 * 
 * @author Mark
 * 
 */
public class ObjectMessageEncoder implements MessageEncoder {
	public ByteBuffer encode(NetMessage msg) {
		ByteBuffer buffer = new ByteBuffer(512);

		ObjectMessage message;
		if (msg == null) {
			message = new ObjectMessage(new byte[0]);
		} else {
			message = (ObjectMessage) msg;
		}

		byte[] bytes = message.getBytes();

		buffer.writeInt(bytes.length);
		buffer.writeBytes(bytes);

		return buffer;
	}
}