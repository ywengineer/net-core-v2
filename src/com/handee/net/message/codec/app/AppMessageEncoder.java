package com.handee.net.message.codec.app;

import com.handee.net.message.AppMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageEncoder;
import com.handee.utils.ByteBuffer;

/**
 * 
 * 消息编码器。
 * 
 * @author Mark
 * 
 */
public class AppMessageEncoder implements MessageEncoder {
	public ByteBuffer encode(NetMessage msg) {
		ByteBuffer buffer = new ByteBuffer(512);

		AppMessage appMsg = (AppMessage) msg;
		if (appMsg == null)
			appMsg = new AppMessage(AppMessage.MESSAGE_TYPE_NULL, new ByteBuffer(0), null);
		int type = appMsg.getType();
		ByteBuffer msgBuffer = appMsg.getBuffer();
		int length = msgBuffer == null ? 0 : msgBuffer.available();
		buffer.writeShort(length + 2);
		buffer.writeShort(type);
		if (msgBuffer != null) {
			int pos = msgBuffer.position();
			buffer.writeByteBuffer(msgBuffer, length);
			msgBuffer.position(pos);
		}

		return buffer;
	}

}