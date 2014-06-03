/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * RpcClient.java
 *
 * 14-1-22 上午10:20
 *
 *******************************************************************/
package com.handee.rpc;

import com.handee.job.JobProcessCallback;
import com.handee.job.RunnableNetJob;
import com.handee.rpc.rmi.ObjectSpace;
import com.handee.utils.SystemUtils;
import com.handee.utils.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * com.handee.rpc.RpcClient.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-1-22 上午10:20
 */
public class RpcClient extends BaseRpcService {
	private static final Logger logger = Logger.getLogger(RpcClient.class);

	private RpcClient(EndPoint endpoint) throws Exception {
		super(endpoint);
	}

	/**
	 * 在指定端口创建Rpc服务。
	 * <p/>
	 * 在创建成功之后,需要先start,再connect.
	 * <p/>
	 * 默认写入和读取缓冲区大小为10240.
	 * 
	 * @return Rpc服务
	 */
	public static RpcClient create() {
		return create(65535, 65535);
	}

	/**
	 * 在指定端口，以指定缓冲区大小创建一个Rpc服务。
	 * <p/>
	 * 在创建成功之后,需要先start,再connect.
	 * <p/>
	 * 以默认的{@link KryoSerialization}为消息序列化器。
	 * 
	 * @param writeBufferSize
	 *            write buffer size.
	 * @param objectBufferSize
	 *            read buffer size.
	 * @return Rpc服务
	 */
	public static RpcClient create(int writeBufferSize, int objectBufferSize) {
		return create(writeBufferSize, objectBufferSize, new KryoSerialization());
	}

	/**
	 * 在指定端口，以及指定缓冲区大小和消息序列化器创建Rpc服务。
	 * <p/>
	 * 在创建成功之后,需要先start,再connect.
	 * 
	 * @param writeBufferSize
	 *            write buffer size.
	 * @param objectBufferSize
	 *            read buffer size.
	 * @param serialization
	 *            serialization factory.
	 * @return Rpc Service.
	 */
	public static RpcClient create(int writeBufferSize, int objectBufferSize, Serialization serialization) {
		try {
			Client client = new Client(writeBufferSize, objectBufferSize, serialization);
			RpcClient service = new RpcClient(client);
			SystemUtils.printSection("rpc client create succeed");
			return service;
		} catch (Exception e) {
			logger.error("rpc client create failed.", e);
		}
		return null;
	}

	/**
	 * Opens a TCP only client.
	 * <p/>
	 * default timeout is 5000ms.
	 * 
	 * @param host
	 *            主机
	 * @param tcpPort
	 *            端口
	 */
	public void connect(String host, int tcpPort, final JobProcessCallback<Boolean> callback) {
		this.connect(5000, host, tcpPort, callback);
	}

	/**
	 * Opens a TCP only client.
	 * 
	 * @param timeout
	 *            超时时间
	 * @param host
	 *            主机
	 * @param tcpPort
	 *            端口
	 */
	public void connect(final int timeout, final String host, final int tcpPort, final JobProcessCallback<Boolean> callback) {
		Utils.runAsyn(new RunnableNetJob<Boolean>() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see com.handee.job.RunnableNetJob#process()
			 */
			@Override
			protected Boolean process() {
				try {
					Client client = (Client) endPoint;
					client.connect(timeout, host, tcpPort);
					logger.info("rpc client connected to server [ip=" + host + ", port=" + tcpPort + "]");
					return Boolean.TRUE;
				} catch (IOException e) {
					logger.error("rpc client connected to server failure, [ip=" + host + ", port=" + tcpPort + "]", e);
				}
				// shutdown service.
				shutdown();
				// exit this application.
				System.exit(0);
				return Boolean.FALSE;
			}
		}, callback);
	}

	/**
	 * 获取Rpc服务
	 * 
	 * @param objectId
	 *            服务ID
	 * @param iface
	 *            服务接口
	 * @param <T>
	 *            服务 接口定义
	 * @return 远程服务调用接口
	 */
	public <T> T getRemoteObject(int objectId, Class<T> iface) {
		return ObjectSpace.getRemoteObject((Client) this.endPoint, objectId, iface);
	}
}