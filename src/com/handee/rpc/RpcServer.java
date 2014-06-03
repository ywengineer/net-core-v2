/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * Rpc.java
 *
 * 14-1-21 下午3:08
 *
 *******************************************************************/
package com.handee.rpc;

import com.handee.rpc.rmi.ObjectSpace;
import org.apache.log4j.Logger;
import org.apache.mina.util.ConcurrentHashSet;

import java.util.Set;

/**
 * com.handee.rpc.Rpc.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-1-21 下午3:08
 */
public class RpcServer extends BaseRpcService implements RpcServiceListener {
    private static final Logger logger = Logger.getLogger(RpcServer.class);
    private Set<Connection> connections = new ConcurrentHashSet<>();

    private RpcServer(EndPoint endpoint) throws Exception {
        super(endpoint);
    }

    /**
     * 在指定端口创建Rpc服务。
     * <p/>
     * 默认写入和读取缓冲区大小为10240.
     *
     * @param port 端口号
     * @return Rpc服务
     */
    public static RpcServer create(int port) {
        return create(port, 65535, 65535);
    }

    /**
     * 在指定端口，以指定缓冲区大小创建一个Rpc服务。
     * <p/>
     * 以默认的{@link KryoSerialization}为消息序列化器。
     *
     * @param port             端口号。
     * @param writeBufferSize  write buffer size.
     * @param objectBufferSize read buffer size.
     * @return Rpc服务
     */
    public static RpcServer create(int port, int writeBufferSize, int objectBufferSize) {
        return create(port, writeBufferSize, objectBufferSize, new KryoSerialization());
    }

    /**
     * 在指定端口，以及指定缓冲区大小和消息序列化器创建Rpc服务。
     *
     * @param port             端口
     * @param writeBufferSize  write buffer size.
     * @param objectBufferSize read buffer size.
     * @param serialization    serialization factory.
     * @return Rpc Service.
     */
    public static RpcServer create(int port, int writeBufferSize, int objectBufferSize, Serialization serialization) {
        try {
            if (port <= 0 || port > 65535) {
                logger.error("rpc port can not less than zero and greater than 65535, current port = " + port);
                return null;
            }
            Server server = new Server(writeBufferSize, objectBufferSize, serialization);
            RpcServer service = new RpcServer(server);
            server.bind(port);
            service.addListener(service);
            return service;
        } catch (Exception e) {
            logger.error("rpc service create failed.", e);
        }
        return null;
    }

    /**
     * 获取与指定连接相关Rpc服务
     *
     * @param connection rpc连接
     * @param objectId   服务ID
     * @param iface      服务接口
     * @param <T>        服务
     *                   接口定义
     * @return 远程服务调用接口
     */
    public <T> T getRemoteObject(Connection connection, int objectId, Class<T> iface) {
        return ObjectSpace.getRemoteObject(connection, objectId, iface);
    }

    /**
     * RPC服务关闭之后的处理。
     *
     * @see RpcServiceListener#onStop()
     */
    @Override
    public void onStop() {
        connections.clear();
        connections = null;
    }

    /**
     * @param connection rpc连接
     * @param object     消息
     * @see com.handee.rpc.RpcServiceListener#onMessageReceived(com.handee.rpc.Connection, Object)
     */
    @Override
    public void onMessageReceived(Connection connection, Object object) {

    }

    /**
     * @param connection rpc连接
     * @see com.handee.rpc.RpcServiceListener#onIdle(com.handee.rpc.Connection)
     */
    @Override
    public void onIdle(Connection connection) {

    }

    /**
     * @param connection rpc连接
     * @see com.handee.rpc.RpcServiceListener#onDisconnected(com.handee.rpc.Connection)
     */
    @Override
    public void onDisconnected(Connection connection) {
        connections.remove(connection);
    }

    /**
     * @param connection rpc连接
     * @see com.handee.rpc.RpcServiceListener#onConnected(com.handee.rpc.Connection)
     */
    @Override
    public void onConnected(Connection connection) {
        connections.add(connection);
    }
}
