package com.handee.network;

import com.handee.net.message.NetMessage;
import com.handee.utils.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;

/**
 * 
 * 使用MINA框架实现的网络连接类
 * 
 * @author Mark
 * 
 */
public class MinaConnection extends NetConnection {
	private IoSession ioSession;

	public MinaConnection(IoSession session) {
		ioSession = session;
		sessionId = ioSession.getId();
		InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
		this.info = new ConnectionInfo(remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort(), ((InetSocketAddress) session.getLocalAddress()).getPort());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.handee.network.NetConnection#sendMessage(com.handee.net.message.HDMessage
	 * )
	 */
	@Override
	public void sendMessage(NetMessage msg) {
		if (getMessageEncoder() == null) {
			ioSession.write(msg);
			return;
		}
		ByteBuffer sendBuffer = getMessageEncoder().encode(msg);
		sendData(sendBuffer.getRawBytes(), 0, sendBuffer.length());
	}

	protected void sendDataImpl(byte[] data, int offset, int count) {
		IoBuffer buffer = IoBuffer.allocate(count);
		buffer.put(data, offset, count);
		buffer.flip();
		if (ioSession != null && !ioSession.isClosing()) {
			ioSession.write(buffer);
		}
	}

	public void close() {
		if (ioSession != null){
			ioSession.close(false);
		}
		ioSession = null;
	}

	public boolean isActive() {
		return ioSession != null;
	}
}
