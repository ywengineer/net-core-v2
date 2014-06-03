/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ServiceListener.java
 *
 * 14-1-22 上午11:50
 *
 *******************************************************************/
package com.handee.rpc;

/**
 * com.handee.rpc.ServiceListener.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-1-22 上午11:50
 */
public interface RpcServiceListener {
    /**
     * RPC服务关闭之后的处理。
     */
    public abstract void onStop();

    /**
     * 收到rpc消息
     *
     * @param connection rpc连接
     * @param object     消息
     */
    public abstract void onMessageReceived(Connection connection, Object object);

    /**
     * rpc连接空闲。
     *
     * @param connection rpc连接
     */
    public abstract void onIdle(Connection connection);

    /**
     * 失去rpc连接。
     *
     * @param connection rpc连接
     */
    public abstract void onDisconnected(Connection connection);

    /**
     * 连接创建成功，或者连接成功。
     *
     * @param connection rpc连接
     */
    public abstract void onConnected(Connection connection);
}
