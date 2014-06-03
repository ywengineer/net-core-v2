package com.handee.network;


import com.handee.utils.ByteBuffer;
import com.handee.utils.DataReader;
import com.handee.utils.DataWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SocketChannel;




/**

类说明:网络连接

@version 1.0
@author daff

*/
public class TcpSocket
{
	private DataReader reader;
	private DataWriter writer;
	private InputStream in;
	private OutputStream out;
	private Socket socket;
	private boolean active = true;

	/**
	 * 构造一个连接到指定主机的指定断口的连接
	 */
	public TcpSocket(String host, int port)throws IOException
	{
		this(new Socket(host, port));
	}
	/**
	 * 从一个SOCKET创建一个连接
	 */
	public TcpSocket(Socket socket)throws IOException
	{
		this.socket = socket;
		in = socket.getInputStream();
		out = socket.getOutputStream();
		reader = new DataReader(in);
		writer = new DataWriter(out);
	}
	/**
	 * 通过代理服务器连接
	 */
	public TcpSocket(String proxyHost, int proxyPort, String targetHost, int targetPort)throws IOException
	{
		this(new Socket(proxyHost, proxyPort), targetHost, targetPort);
	}
	/**
	 * 通过代理服务器连接
	 */
	public TcpSocket(String proxyHost, int proxyPort, String user, String pass, String targetHost, int targetPort)throws IOException
	{
		this(new Socket(proxyHost, proxyPort), user, pass, targetHost, targetPort);
	}
	/**
	 * 构造一个通过代理服务器的连接
	 */
	@SuppressWarnings("unused")
	public TcpSocket(Socket socket,String host, int port)throws IOException
	{
		in = socket.getInputStream();
		out = socket.getOutputStream();
		reader = new DataReader(in);
		writer = new DataWriter(out);

		//请求代理
		byte[] data ={ 5, 1, 0 };
		out.write(data);
		int res = reader.readShort();
		if (res != 5)
			throw new IOException("proxy not support!");
		//目标地址和端口，注意端口字节的顺序是反着的
		data = new byte[] { 5, 1, 0, 3 };
		out.write(data);
		data = host.getBytes();
		out.write(data.length);
		out.write(data);
		out.write(port >> 8);
		out.write(port & 0xff);
		res = reader.readInt();
		if (res != (5 | 1 << 24))
			throw new IOException("proxy failed");

		byte[] myIpData = reader.readBytes(4);
		int b1 = in.read(), b2 = in.read();
		int proxyPort = b1 << 8 | b2;

		this.socket = socket;
	}
	/**
	 * 构造一个通过代理服务器的连接，需要验证密码
	 */
	@SuppressWarnings("unused")
	public TcpSocket(Socket socket, String user, String pass, String host, int port)throws IOException
	{
		in = socket.getInputStream();
		out = socket.getOutputStream();
		reader = new DataReader(in);
		writer = new DataWriter(out);

		//请求代理
		byte[] data ={ 5, 2, 0, 2 };
		out.write(data);
		int res = reader.readShort();
		if (res != (5 | 2 << 8))
			throw new IOException("proxy not support!");

		//认证
		out.write(1);
		data = user.getBytes();
		out.write(data.length);
		out.write(data);
		data = pass.getBytes();
		out.write(data.length);
		out.write(data);
		res = reader.readShort();
		if (res != 1)
			throw new IOException("invalid username or password");

		//目标地址和端口，注意端口字节的顺序是反着的
		data = new byte[] { 5, 1, 0, 3 };
		out.write(data);
		data = host.getBytes();
		out.write(data.length);
		out.write(data);
		out.write((port >> 8) & 0xff);
		out.write(port & 0xff);
		res = reader.readInt();
		if (res != (5 | 1 << 24))
			throw new IOException("proxy failed");

		byte[] myIpData = reader.readBytes(4);
		int b1 = in.read(), b2 = in.read();
		int proxyPort = b1 << 8 | b2;

		this.socket = socket;
	}
	public SocketChannel getChannel()
	{
		return socket.getChannel();
	}
	public DataReader getReader()
	{
		return reader;
	}
	public DataWriter getWriter()
	{
		return writer;
	}
	/**
     * 发送数据
	 * 发送数据要用Socket所实现的OutputStream，而不要用DataWriter，后者速CPU占用很高
     */
	public boolean send(byte[] data, int offset, int count)
	{
		if (!active)
			return false;
		//synchronized (out)
		{
			try
			{
				out.write(data, offset, count);
				out.flush();
				return true;
			}
			catch (IOException e)
			{
				active = false;
			}
		}
		return false;
	}
	/**
     * 发送数据
     */
	public boolean send(ByteBuffer buffer)
	{
		byte[] data = buffer.getRawBytes();
		int pos = buffer.position();
		int count = buffer.available();
		return send(data, pos, count);
	}
	/**
	 * 取出缓冲区的数据
	 */
	public byte[] peek()
	{
		if (!active)
			return null;
		if (in == null)
			return null;
		//synchronized (in)
		{
			try
			{
				int count = in.available();
				byte[] data = new byte[count];
				count = in.read(data, 0, data.length);
				while (count < data.length)
				{
					count += in.read(data, count, data.length - count);
				}
				return data;
			}
			catch (IOException e)
			{
				active = false;
			}
		}
		return null;
	}
	/**取出缓冲区的数据*/
	public int peek(byte[] buff)
	{
		if (!active)
			return 0;
		if (in == null)
			return 0;
		//synchronized (in)
		{
			try
			{
				int count = Math.min(buff.length, in.available());
				for (int i = 0; i < count; i++)
					buff[i] = (byte)in.read();
				return count;
			}
			catch (Exception e)
			{
				active = false;
			}
		}
		return 0;
	}
	/**是否有数据可以读出*/
	public boolean hasData()
	{
		if (in == null || !active)
			return false;
		try
		{
			return in.available() > 0;
		}
		catch (IOException e)
		{
			active = false;
			return false;
		}
	}
	/**
	 * 获取可读数据
	 */
	public int available()
	{
		if (in == null || !active)
			return 0;
		try
		{
			return in.available();
		}
		catch (IOException e)
		{
			active = false;
		}
		return 0;
	}
	/**是否活动*/
	public boolean active()
	{
		return active;
	}
	/**断开连接*/
	public synchronized void close()
	{
		active = false;
		if (socket == null)
			return;
		try
		{
			in.close();
			out.close();
			socket.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			reader = null;
			writer = null;
			in = null;
			out = null;
			socket = null;
		}
	}

	public String getHost()
	{
		if (socket != null)
			return socket.getInetAddress().getHostAddress();
		return null;
	}
	public byte[] getHostIp()
	{
		if (socket != null)
			return socket.getInetAddress().getAddress();
		return null;
	}
	public int getPort()
	{
		if (socket != null)
			return socket.getPort();
		return 0;
	}
	public String getLocalHost()
	{
		if (socket != null)
			return socket.getLocalAddress().getHostAddress();
		return null;
	}
	public byte[] getLocalIp()
	{
		if (socket != null)
			return socket.getLocalAddress().getAddress();
		return null;
	}
	public int getLocalPort()
	{
		if (socket != null)
			return socket.getLocalPort();
		return 0;
	}

	public String toString()
	{
		if (socket != null)
			return "remoteIP=" + socket.getInetAddress().getHostAddress() + ";remotePort=" + socket.getPort() + ";localPort=" + socket.getLocalPort();
		return "";
	}
}