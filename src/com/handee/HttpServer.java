package com.handee;

import com.handee.mina.codec.http.HttpServerProtocolCodecFactory;
import com.handee.net.message.HttpRequestMessage;
import com.handee.network.NetConnection;
import com.handee.utils.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;

/**
 * 
 * @author Mark
 * 
 */
public class HttpServer extends BaseServer implements IoHandler {
	private static final Logger logger = Logger.getLogger(HttpServer.class);

	public HttpServer(int port) {
		super(port, new HttpServerProtocolCodecFactory());
	}

	public final void start() {
		try {
			if (isRunning()) {
				return;
			}
			acceptor.setHandler(this);
			acceptor.bind(new InetSocketAddress(port));
			running = true;
			SystemUtils.printSection("http service listening on port :: " + port);
		} catch (Exception ex) {
			logger.error("start http service error :: ", ex);
			running = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#messageReceived(org.apache.mina
	 * .core.session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		if (message != null && message instanceof HttpRequestMessage) {
			handlerWrapper.httpMessageArrived(session, (HttpRequestMessage) message);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionOpened(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		logger.info("sessionOpened in http server[" + session.getId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandlerAdapter#sessionClosed(org.apache
	 * .mina.core.session.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("sessionClosed in http server[" + session.getId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionCreated(org.apache.mina
	 * .core.session.IoSession)
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		logger.info("sessionCreated in http server[" + session.getId() + "]");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#sessionIdle(org.apache.mina.core
	 * .session.IoSession, org.apache.mina.core.session.IdleStatus)
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#exceptionCaught(org.apache.mina
	 * .core.session.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		session.close(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.mina.core.service.IoHandler#messageSent(org.apache.mina.core
	 * .session.IoSession, java.lang.Object)
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.handee.IServer#getConnectionCount()
	 */
	@Override
	public int getConnectionCount() {
		return acceptor.getManagedSessionCount();
	}

	@Override
	public NetConnection getConnection(long sessionId) {
		return null;
	}
}
