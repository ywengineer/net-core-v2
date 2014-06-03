/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * AppMessageEncoder.java
 * 
 * 2013 2013-6-8 上午10:35:17
 * 
 *******************************************************************/
package com.handee.mina.codec.app;

import com.handee.net.message.AppMessage;
import com.handee.utils.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class AppMessageEncoder implements MessageEncoder<AppMessage> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.demux.MessageEncoder#encode(org.apache.mina
	 * .core.session.IoSession, java.lang.Object,
	 * org.apache.mina.filter.codec.ProtocolEncoderOutput)
	 */
	@Override
	public void encode(IoSession session, AppMessage message, ProtocolEncoderOutput out) throws Exception {
		// message command
		int type = message.getType();
		// encoded message bytes
		ByteBuffer msgBuffer = message.getBuffer();
		// size of message data.
		int length = msgBuffer == null ? 0 : msgBuffer.available();
		// allocate a buffer.
		IoBuffer buffer = IoBuffer.allocate(length + 4);
		// size of package.
		buffer.putUnsignedShort(length + 2);
		// write message command.
		buffer.putUnsignedShort(type);
		// if this message have data.
		if (msgBuffer != null) {
			// read position.
			int pos = msgBuffer.position();
			// write all data of this message.
			buffer.put(msgBuffer.getBytes());// writeByteBuffer(msgBuffer,
												// length);
			// reset position of message data buffer.
			msgBuffer.position(pos);
		}
		// flip buffer.
		buffer.flip();
		// write to client.
		out.write(buffer);
	}

}
