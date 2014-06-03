package com.handee.network;

import java.nio.channels.SocketChannel;

/**
 * 
 * 使用原生NIO框架实现的网络连接。
 * 
 * @author Mark
 * 
 */
public class SimpleConnection extends NetConnection {
	/** 数据缓冲区 */
	private byte[] bufferData;
	/** 连接信息 */
	private TcpSocket socket;

	/**
	 * 新建网络连接
	 * 
	 * @param conn
	 *            连接信息
	 */
	public SimpleConnection(TcpSocket conn) {
		this(conn, 128);
	}

	/**
	 * 新建网络连接
	 * 
	 * @param conn
	 *            连接信息
	 * @param bufferSize
	 *            初始数据缓冲区大小
	 */
	public SimpleConnection(TcpSocket conn, int bufferSize) {
		socket = conn;
		bufferData = new byte[bufferSize];
		info = new ConnectionInfo(conn.getHost(), conn.getPort(), conn.getLocalPort());
		sessionId = idGenerator.incrementAndGet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.network.NetConnection#sendDataImpl(byte[], int, int)
	 */
	protected void sendDataImpl(byte[] data, int offset, int count) {
		if (!isActive())
			return;
		socket.send(data, offset, count);
		idleTime = 0;
	}

	public TcpSocket getSocket() {
		return socket;
	}

	public void close() {
		socket.close();
	}

	public String getIP() {
		return null;
	}

	public boolean isActive() {
		return socket.active();
	}

	public SocketChannel getChannel() {
		return socket.getChannel();
	}

	public boolean hasData() {
		return socket.hasData();
	}

	public void processPeek() {
		processPeek(System.currentTimeMillis());
	}

	public void processPeek(long currentTime) {
		if (socket.hasData()) {
			int count = socket.peek(bufferData);
			onDataRead(bufferData, 0, count);
		} else {
			idle(currentTime);// 发送一个心跳消息
		}
	}

	public String toString() {
		return "SimpleConnection[" + socket.toString() + "]";
	}
}
