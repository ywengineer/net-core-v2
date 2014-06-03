package com.handee.network;

import com.handee.net.message.codec.MessageCodecFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 网络服务器
 */
public abstract class NetServer {
	private int port;
	private ConcurrentLinkedQueue<INetServerListener> listeners = new ConcurrentLinkedQueue<>();
	private int maxConnections;
	private MessageCodecFactory factory;
	private MessageQueue messageQueue;

	public NetServer() {
	}

	public void init(int port, boolean useMessageQueue) {
		this.port = port;
		if (useMessageQueue)
			messageQueue = new MessageQueue();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMaxConnections(int max) {
		maxConnections = max;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMessageCodecFactory(MessageCodecFactory factory) {
		this.factory = factory;
	}

	public MessageCodecFactory getMessageCodecFactory() {
		return factory;
	}

	public void addNetServerListener(INetServerListener l) {
		listeners.add(l);
	}

	public void removeNetServerListener(INetServerListener l) {
		listeners.remove(l);
	}

	public void removeAllNetServerListeners() {
		listeners.clear();
	}

	public void closeAllConnections() {

	}

	public abstract int getConnectionCount();

	public abstract boolean start();

	public abstract void stop();

	protected void connectionOpened(NetConnection conn) {
		if (factory != null) {
			conn.setMessageDecoder(factory.createDecoder());
			conn.setMessageEncoder(factory.createEncoder());
		}
		if (messageQueue != null)
			conn.setMessageQueue(messageQueue);

		for (INetServerListener l : this.listeners) {
			l.connectionOpened(conn);
		}
	}

	protected void connectionClosed(NetConnection conn) {
		conn.setMessageEncoder(null);
		conn.setMessageDecoder(null);
		conn.setMessageQueue(null);

		for (INetServerListener l : this.listeners) {
			l.connectionClosed(conn);
		}
		conn.removeAllListeners();
	}
}
