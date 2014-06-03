/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * BufferMessageCodecFactory.java
 * 
 * 2013 2013-5-23 下午1:20:29
 * 
 *******************************************************************/
package com.handee.net.message.codec.buffer;

import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.net.message.codec.MessageEncoder;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class ByteArrayMessageCodecFactory extends MessageCodecFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.net.message.codec.MessageCodecFactory#createDecoder()
	 */
	@Override
	public MessageDecoder createDecoder() {
		return new ObjectMessageDecoder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.net.message.codec.MessageCodecFactory#createEncoder()
	 */
	@Override
	public MessageEncoder createEncoder() {
		return new ObjectMessageEncoder();
	}

}
