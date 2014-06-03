package com.handee.network;


import com.handee.utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;




/**

类说明:网络连接工厂

@version 1.0
@author daff

*/
public final class SocketFactory
{
	private ServerSocket server;
	private Thread runner;
	private SocketConsumer consumer;
	private boolean stopOnError = true;

	public SocketFactory(int port, SocketConsumer consumer)throws IOException
	{
		this(new ServerSocket(port), consumer);
	}

	public SocketFactory(ServerSocket server, SocketConsumer consumer)
	{
		this.server = server;
		this.consumer = consumer;
	}

	public void setStopOnError(boolean b)
	{
		stopOnError = b;
	}

	public boolean getStopOnError()
	{
		return stopOnError;
	}

	public void start()
	{
		if (runner == null)
		{
			runner = new ListenThread();
			runner.start();
		}
	}

	public void stop()throws IOException
	{
		if (runner != null)
		{
			runner.interrupt();
			runner = null;
		}
	}

	private class ListenThread extends Thread
	{
		public void run()
		{
			while (runner == this)
			{
				if (consumer.getAcceptDelay() > 0)
					Utils.sleep(consumer.getAcceptDelay());
				try
				{
					Socket socket = server.accept();
					if (consumer == null || !consumer.consumeSocket(socket))
						socket.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
					if (stopOnError)
						break;
				}
			}
			try
			{
				server.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			runner = null;
		}
	}
}