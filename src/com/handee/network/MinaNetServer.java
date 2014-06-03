package com.handee.network;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MinaNetServer 使用MINA框架实现的网络服务器
 */
public class MinaNetServer extends NetServer implements IoHandler {
	private static final Logger logger = Logger.getLogger(MinaNetServer.class);
	protected IoAcceptor acceptor;
	protected int connectionCount;

	public void exceptionCaught(IoSession session, Throwable cause) {
		logger.error("error in data transport :: ", cause);
		MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		conn.close();
	}

	public void messageReceived(IoSession session, Object message) {
		if (!(message instanceof IoBuffer)) {
			return;
		}
		MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		IoBuffer buffer = (IoBuffer) message;
		int len = buffer.remaining();
		byte[] data = new byte[len];
		buffer.get(data, 0, data.length);
		buffer.free();
		conn.onDataRead(data, 0, len);
	}

	public void messageSent(IoSession session, Object message) {
	}

	public void sessionClosed(IoSession session) {
		connectionCount--;
		MinaConnection conn = (MinaConnection) session.getAttribute("conn");
		session.setAttribute("conn", null);
		connectionClosed(conn);
	}

	public void sessionCreated(IoSession session) {
	}

	public void sessionIdle(IoSession session, IdleStatus status) {
	}

	public void sessionOpened(IoSession session) {

		connectionCount++;
		MinaConnection connection = new MinaConnection(session);
		session.setAttribute("conn", connection);
		connectionOpened(connection);
	}

	public int getConnectionCount() {
		return connectionCount;
	}

	public boolean start() {
		acceptor = new NioSocketAcceptor();

		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.setHandler(this);

		try {
			acceptor.bind(new InetSocketAddress(getPort()));
		} catch (java.io.IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 停止服务。
	 * 
	 * <b><font color=#FF0000>注意谨慎使用:立即释放当前所有连接资源</font></b>
	 * 
	 */
	public void stop() {
		// acceptor.unbind();
		if (acceptor.isDisposed())
			return;

		if (acceptor.isDisposing()) {
			return;
		}
		acceptor.dispose();
	}

	public List<IoSession> getAllSession() {
		List<IoSession> all = new ArrayList<>();
		if (acceptor != null && acceptor.isActive()) {
			all.addAll(acceptor.getManagedSessions().values());
		}
		return Collections.unmodifiableList(all);
	}
}