package com.handee.net.message.codec.serializable;

import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.net.message.codec.MessageEncoder;

/**
 * 
 * 序列化对象编码，解码工厂。
 * 
 * <p>
 * 仅支持原生的Java序列化
 * </p>
 * 
 * @author Mark
 * 
 */
public class SerializableMessageCodecFactory extends MessageCodecFactory {
	public MessageDecoder createDecoder() {
		return new SerializableMessageDecoder();
	}

	public MessageEncoder createEncoder() {
		return new SerializableMessageEncoder();
	}
}