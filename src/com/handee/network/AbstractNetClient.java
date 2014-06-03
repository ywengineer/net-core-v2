/******************************************************************* 
 * @Version : 0.5 
 
 *
 * AbstractNetClient.java 
 *
 *
 * 2011-10-11 上午09:16:54
 *
 *******************************************************************/
package com.handee.network;

import com.handee.net.message.AppMessage;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.net.message.codec.MessageEncoder;
import com.handee.utils.SystemUtils;
import com.handee.utils.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author wang
 * 
 */
public abstract class AbstractNetClient implements Runnable, INetMessageListener {

	protected Logger _log = Logger.getLogger(this.getClass());

	protected SimpleConnection connection = null;

	private String ip;

	private int port;

	private MessageDecoder decoder;

	private MessageEncoder encoder;

	private Thread t;

	private boolean running = false;

	private boolean isFirst = true;

	public AbstractNetClient(String ip, int port, MessageDecoder decoder, MessageEncoder encoder) throws IOException {
		this.ip = ip;
		this.port = port;
		this.decoder = decoder;
		this.encoder = encoder;
		running = true;
		initConnection();
	}

	public void start() {
		t = new Thread(this, this.getClass().getName());
		t.start();
	}

	public void run() {
		while (running) {
			if (connection == null || !connection.isActive()) {
				SystemUtils.printSection("try connection to " + getIp() + " port:" + getPort());
				try {
					initConnection();// 重置连接
				} catch (Exception ex) {
					// 这个连接时间在游戏运行中,所以不做处理
				}
				Utils.sleep(60 * 1000);// 1分钟后重试
			} else {
				if (isFirst) {
					onFisrtConncetion();
					isFirst = false;
				}
				connection.processPeek();
			}
		}
	}

	public void messageArrived(NetConnection conn, NetMessage msg) {
		if (isRunning()) {
			AppMessage appMsg = (AppMessage) msg;
			if (appMsg.getType() == AppMessage.MESSAGE_TYPE_NULL) {
				return;
			}
			messageArrivedImpl(conn, appMsg);
		}
	}

	/**
	 * 当第一次连接成功
	 */
	public abstract void onFisrtConncetion();

	/**
	 * 当每一次连接成功
	 */
	public abstract void onConnection();

	public abstract void messageArrivedImpl(NetConnection conn, AppMessage appMsg);

	/**
	 * 初始化连接
	 * 
	 * @throws IOException
	 */
	protected void initConnection() throws IOException {
		if (isActive())
			return;
		try {
			TcpSocket socket = new TcpSocket(ip, port);
			connection = new SimpleConnection(socket);
			connection.setPingTime(0);
			connection.setTimeout(5 * 60 * 1000);
			connection.setMessageDecoder(decoder);
			connection.setMessageEncoder(encoder);
			connection.addListener(this);
			running = true;
			SystemUtils.printSection("connectioned to " + getIp() + " port:" + getPort());
			onConnection();
		} catch (IOException e) {
			connection = null;
			_log.error("连接到" + ip + " 端口:" + port + " 发生错误");
			throw e;
		}
	}

	public void sendMessage(NetMessage msg) {
		if (isActive()) {
			connection.sendMessage(msg);
		}
	}

	public boolean isActive() {
        return connection != null && connection.isActive();
	}

	public String getIp() {
		return ip;
	}

	public boolean isRunning() {
		return running;
	}

	public int getPort() {
		return port;
	}
}
