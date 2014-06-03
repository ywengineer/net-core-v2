package com.handee.network;

import com.handee.net.message.NetMessage;

import java.util.LinkedList;

/**
 * MessageQueue 消息队列
 */
public class MessageQueue implements Runnable {
	private final LinkedList<NetMessage> messageList = new LinkedList<>();
	private Thread runner;

	public MessageQueue() {
		runner = new Thread(this);
		runner.start();
	}

	public void post(NetMessage msg) {
		synchronized (messageList) {
			messageList.add(msg);
			messageList.notify();
		}
	}

	public NetMessage pop() {
		synchronized (messageList) {
			try {
				if (messageList.size() <= 0)
					messageList.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
			if (messageList.size() <= 0)
				return null;
			return messageList.remove();
		}
	}

	public NetMessage peek() {
		if (messageList.size() <= 0)
			return null;
		synchronized (messageList) {
			return messageList.remove();
		}
	}

	public void stop() {
		synchronized (messageList) {
			messageList.clear();
			messageList.notify();
		}
		runner = null;
	}

	public void run() {
		while (runner != null) {
			NetMessage msg = pop();
			if (msg == null)
				break;
			if (!(msg.getSource() instanceof NetConnection)) {
				System.out.println("wrong source:" + msg.getSource());
				continue;
			}
			NetConnection conn = (NetConnection) msg.getSource();
			conn.dispatchMessage(msg);
		}
		runner = null;
	}
}