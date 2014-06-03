package com.handee.net.message.codec.app;

import com.handee.net.message.AppMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.utils.ByteBuffer;

/**
 * 
 * 消息解码器。
 * 
 * @author Mark
 * 
 */
public class AppMessageDecoder implements MessageDecoder {
	public NetMessage decode(ByteBuffer buffer) {
		if (buffer.available() < 4)
			return null;
		int position = buffer.position();
		int length = buffer.readUnsignedShort();
		if (buffer.available() < length) {
			buffer.position(position);
			return null;
		}
		if (length < 0) {
			System.out.println("nagative length:" + length);
			System.out.println("buffer available:" + buffer.available());
			return null;
		}
		int type = buffer.readUnsignedShort();
		AppMessage message = new AppMessage(type, length - 2);
		message.getBuffer().writeByteBuffer(buffer, length - 2);
		return message;
	}

	public NetMessage decode(NetMessage message, ByteBuffer buffer) {

		return null;
	}
}