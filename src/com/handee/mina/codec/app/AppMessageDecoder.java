/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * AppMessageDecoder.java
 * 
 * 2013 2013-6-8 上午10:34:28
 * 
 *******************************************************************/
package com.handee.mina.codec.app;

import com.handee.net.message.AppMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class AppMessageDecoder extends MessageDecoderAdapter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.demux.MessageDecoder#decodable(org.apache
	 * .mina.core.session.IoSession, org.apache.mina.core.buffer.IoBuffer)
	 */
	@Override
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
		if (in.remaining() < 4)
			return MessageDecoderResult.NEED_DATA;
		// current position.
		int position = in.position();
		// read an number represent the size of message.
		int length = in.getUnsignedShort();
		// if size less than and equals zero or the size of this message is not
		// enough.
		if (length <= 0 || in.remaining() < length) {
			// reset position to original.
			in.position(position);
			// need more data.
			return MessageDecoderResult.NEED_DATA;
		}
		// the data of this message is enough.
		in.position(position);
		// decode message.
		return MessageDecoderResult.OK;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.demux.MessageDecoder#decode(org.apache.mina
	 * .core.session.IoSession, org.apache.mina.core.buffer.IoBuffer,
	 * org.apache.mina.filter.codec.ProtocolDecoderOutput)
	 */
	@Override
	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int length = in.getUnsignedShort();
		int type = in.getUnsignedShort();
		byte[] data = new byte[length - 2];
		in.get(data);
		AppMessage message = new AppMessage(type, data.length);
		message.getBuffer().writeBytes(data);
		out.write(message);
		return MessageDecoderResult.OK;
	}

}
