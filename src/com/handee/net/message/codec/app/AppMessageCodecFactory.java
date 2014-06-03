package com.handee.net.message.codec.app;

import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.net.message.codec.MessageEncoder;

public class AppMessageCodecFactory extends MessageCodecFactory {
	public MessageDecoder createDecoder() {
		return new AppMessageDecoder();
	}

	public MessageEncoder createEncoder() {
		return new AppMessageEncoder();
	}
}