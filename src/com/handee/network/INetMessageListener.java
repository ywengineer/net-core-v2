package com.handee.network;

import com.handee.net.message.HttpRequestMessage;
import com.handee.net.message.NetMessage;
import org.apache.mina.core.session.IoSession;

/**
 * 
 * 网络连接监听器。
 * 
 * 用于监听是否有新消息到达。
 * 
 * @author Mark
 * 
 */
public interface INetMessageListener {
	/**
	 * 
	 * 收到新消息。
	 * 
	 * @param conn
	 *            当前连接
	 * @param msg
	 *            消息
	 */
	void messageArrived(NetConnection conn, NetMessage msg);

	/**
	 * 
	 * 收到HTTP新消息。
	 * 
	 * @param session
	 *            当前连接
	 * @param message
	 *            HTTP消息。
	 */
	void httpMessageArrived(IoSession session, HttpRequestMessage message);
}
