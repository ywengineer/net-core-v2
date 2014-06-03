package com.handee.net.message.codec;

import com.handee.net.message.NetMessage;
import com.handee.utils.ByteBuffer;

/**
 * 
 * 消息编码器接口
 * 
 * 
 * @author Mark
 * 
 */
public interface MessageEncoder {
	/**
	 * 编码消息
	 * 
	 * @param msg
	 *            消息
	 * @return ByteBuffer
	 */
	public ByteBuffer encode(NetMessage msg);
}
