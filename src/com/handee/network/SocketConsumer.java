package com.handee.network;

import java.net.Socket;

public interface SocketConsumer
{
	public boolean consumeSocket(Socket socket);
	public boolean acceptSocket();
	public int getAcceptDelay();
}