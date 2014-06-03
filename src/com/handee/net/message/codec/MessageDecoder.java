package com.handee.net.message.codec;

import com.handee.net.message.NetMessage;
import com.handee.utils.ByteBuffer;

/**
 * 
 * 消息解码器接口。
 * 
 * @author Mark
 * 
 */
public interface MessageDecoder {
	/**
	 * 解码消息
	 * 
	 * @param buffer
	 *            数据缓冲区
	 * @return 消息
	 */
	public NetMessage decode(ByteBuffer buffer);

	/**
	 * 解码消息
	 * 
	 * @param message
	 *            消息
	 * @param buffer
	 *            数据缓冲区。
	 * @return 消息
	 */
	public NetMessage decode(NetMessage message, ByteBuffer buffer);
}