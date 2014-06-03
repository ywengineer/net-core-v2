package com.handee.network;

/**
 * 
 * 网络服务器监听器。
 * 
 * 主要用于监听新连接打开和连接关闭事件
 * 
 * @author Mark
 * 
 */
public interface INetServerListener {

	/**
	 * 
	 * 连接打开事件。
	 * 
	 * @param connection
	 *            打开的连接
	 */
	void connectionOpened(NetConnection connection);

	/**
	 * 
	 * 连接关闭事件。
	 * 
	 * @param connection
	 *            关闭的连接
	 */
	void connectionClosed(NetConnection connection);
}
