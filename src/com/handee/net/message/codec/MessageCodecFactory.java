package com.handee.net.message.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * 
 * 消息解码器工厂。
 * 
 * 
 * @author Mark
 * 
 */
public abstract class MessageCodecFactory implements ProtocolCodecFactory {
	/**
	 * 创建新的消息解码器
	 * 
	 * @return 解码器
	 */
	public abstract MessageDecoder createDecoder();

	/**
	 * 创建新的消息编码器
	 * 
	 * @return 消息编码器
	 */
	public abstract MessageEncoder createEncoder();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.ProtocolCodecFactory#getEncoder(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.filter.codec.ProtocolCodecFactory#getDecoder(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return null;
	}
}
