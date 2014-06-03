package com.handee.network;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOConnection extends NetConnection {
	private SocketChannel channel;
	private boolean active;
	private int port;
	private String host;
	private int localPort;
	private byte address[];
	private NetServer netServer;

	public NIOConnection(SocketChannel socketchannel, NetServer netserver) {
		active = true;
		channel = socketchannel;
		Socket socket = socketchannel.socket();
		host = socket.getInetAddress().getHostAddress();
		address = socket.getInetAddress().getAddress();
		port = socket.getPort();
		localPort = socket.getLocalPort();
		this.netServer = netserver;
		sessionId = idGenerator.incrementAndGet();
		this.info = new ConnectionInfo(getHost(), getPort(), getLocalPort());
	}

	public String getHost() {
		return host;
	}

	public byte[] getHostIP() {
		return address;
	}

	public int getPort() {
		return port;
	}

	public int getLocalPort() {
		return localPort;
	}

	public boolean isActive() {
		return active;
	}

	public SocketChannel getSocketChannel() {
		return channel;
	}

	public void close() {
		if (!active)
			return;
		active = false;
		try {
			channel.socket().close();
		} catch (IOException ioexception) {
			ioexception.printStackTrace();
		}
	}

	public String toString() {
		return (new StringBuilder()).append("NIOConnection[host=").append(host).append(";port=").append(port).append("]").toString();
	}

	void onClosed() {
		active = false;
	}

	/**
	 * @return the netServer
	 */
	protected final NetServer getNetServer() {
		return netServer;
	}

	protected void sendDataImpl(byte abyte0[], int i, int j) {
		if (!active)
			return;
		ByteBuffer bytebuffer = ByteBuffer.wrap(abyte0, i, j);
		try {
			j = channel.write(bytebuffer);
		} catch (IOException ioexception) {
			active = false;
		}
		// return j;
	}
}
