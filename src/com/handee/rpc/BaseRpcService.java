/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * BaseRpcService.java
 *
 * 14-1-22 上午10:27
 *
 *******************************************************************/
package com.handee.rpc;

import com.esotericsoftware.kryo.Registration;
import com.handee.ContextProperties;
import com.handee.annotations.ManagedByKryo;
import com.handee.rpc.rmi.ObjectSpace;
import com.handee.utils.ClassUtils;
import com.handee.utils.JobQueueOB;
import com.handee.utils.Utils;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * com.handee.rpc.BaseRpcService.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-1-22 上午10:27
 */
public abstract class BaseRpcService extends Listener {
    private static final Logger logger = Logger.getLogger(BaseRpcService.class);
    private boolean running = false;
    private List<RpcServiceListener> listeners = new ArrayList<>();
    protected EndPoint endPoint;
    protected Map<Integer, Object> idToObject = new HashMap<>();
    protected ObjectSpace space;

    BaseRpcService(EndPoint endpoint) throws Exception {
        if (endpoint == null)
            throw new Exception("the value of service end point can not be null");
        this.endPoint = endpoint;
        this.endPoint.addListener(this);
        initialize();
    }

    /**
     * 初始化Rpc服务实例.
     */
    private void initialize() {
        endPoint.getKryo().setRegistrationRequired(true);
        ObjectSpace.registerClasses(endPoint.getKryo());
        endPoint.getKryo().register(IllegalStateException.class);
        endPoint.getKryo().register(IllegalArgumentException.class);
        endPoint.getKryo().register(String[].class);
        endPoint.getKryo().register(int[].class);
        endPoint.getKryo().register(Map.class);
        endPoint.getKryo().register(HashMap.class);
        endPoint.getKryo().register(List.class);
        endPoint.getKryo().register(ArrayList.class);
        endPoint.getKryo().register(Class.class);
    }

    public void addListener(RpcServiceListener listener) {
        if (listener == null)
            return;
        this.listeners.add(listener);
    }

    /**
     * 将指定字节码列表中所有符合注解{@link com.handee.annotations.ManagedByKryo}
     * 的类注册到当前Rpc服务，以供后续快速序列化和传输。
     *
     * @param classes 类描述列表。
     */
    public final void register(Set<Class<?>> classes) {
        List<String> all = new ArrayList<>();
        for (Class<?> cls : classes) {
            if (cls == null) {
                continue;
            }
            // implemented interfaces.
            Class<?>[] interfaces = cls.getInterfaces();
            // register current class to this end point.
            if (cls.getAnnotation(ManagedByKryo.class) != null) {
                all.add(cls.getName());
            } else if (interfaces != null && interfaces.length > 0) {// implements
                // some
                // interfaces
                // iterate
                for (Class<?> iface : interfaces) {
                    // has annotation ManagedByKryo
                    if (iface.getAnnotation(ManagedByKryo.class) != null) {
                        // annotation definition.
                        ManagedByKryo service = iface.getAnnotation(ManagedByKryo.class);
                        // register rpc service.
                        addService(service.objectId(), ClassUtils.getClassInstance(cls));
                        break;
                    }
                }
            }
        }
        // 有序列
        Collections.sort(all);
        // 遍历
        for (String className : all) {
            // 注册
            Registration registration = this.endPoint.getKryo().register(ClassUtils.getClass(className));
            // 日志
            logger.info("Entity [" + className + "] managed by Kryo. Registration id [" + registration.getId() + "]");
        }
    }

    /**
     * 添加一个Rpc服务。
     * <p/>
     * <h2>注意:</h2> 在启动RPC服务之前调用此方法添加服务才生效。
     *
     * @param serviceId 服务ID。
     * @param service   服务提供者。
     */
    public final void addService(int serviceId, Object service) {
        if (idToObject == null) {
            logger.error("rpc service not started");
            return;
        }
        idToObject.put(serviceId, service);
        // if object space already created.
        if (space != null) {
            // register current service to rpc object space.
            space.register(serviceId, service);
        }
        logger.info("RPC service registered [objectId = " + serviceId + ", class = " + service.getClass().getName() + "] .");
    }

    /**
     * 从当前RPC服务器删除与指定参数一样的相关服务。
     *
     * @param serviceId 服务ID
     */
    public final void removeService(int serviceId) {
        if (idToObject == null) {
            logger.error("rpc service not started");
            return;
        }
        // remove from service pool.
        Object removed = this.idToObject.remove(serviceId);
        // service objectId does exist and object space already created.
        if (removed != null && space != null) {
            // remove from service object space.
            space.remove(serviceId);
            // log.
            logger.info("RPC service removed [objectId = " + serviceId + ", class = " + removed.getClass().getName() + "] .");
        }
    }

    /**
     * 从当前RPC服务器删除指定服务。
     *
     * @param object 服务提供者。
     */
    public final void removeService(Object object) {
        if (idToObject == null) {
            logger.error("rpc service not started");
            return;
        }
        Integer key = Utils.removeValue(idToObject, object);
        if (key != null && space != null) {
            space.remove(object);
            logger.info("RPC service registered [objectId = " + key + ", class = " + object.getClass().getName() + "] .");
        }
    }

    /**
     * 获取抽有已在此RPC服务器注册的服务ids.
     *
     * @return 抽有服务相关的ID列表。
     */
    public final Set<Integer> getRegisteredServiceIDs() {
        if (idToObject == null)
            return new HashSet<>();
        return idToObject.keySet();
    }

    /**
     * 删除当前RPC服务器内所有已注册的服务。
     */
    public final void clearServices() {
        for (Integer id : getRegisteredServiceIDs()) {
            removeService(id.intValue());
        }
    }

    /**
     * 启动Rpc服务器。
     */
    public final void start() {
        if (running) {
            logger.error("rpc server is running, can not start running again.");
            return;
        }
        running = true;
        endPoint.start();
    }

    /**
     * 关闭Rpc服务器。
     * <p/>
     * 关闭之后所有注册在此端口上的服务将不可再用。
     */
    public final void shutdown() {
        if (!running) {
            logger.error("rpc server is not running, can not shutdown.");
            return;
        }
        for (RpcServiceListener lis : listeners) {
            lis.onStop();
        }
        running = false;
        endPoint.stop();
        clearServices();
        listeners.clear();
        idToObject.clear();
        if (space != null) {
            space.close();
        }
        listeners = null;
        endPoint = null;
        idToObject = null;
        space = null;
    }

    /**
     * Called when the remote end has been connected. This will be invoked
     * before any objects are received by {@link #received(com.handee.rpc.Connection, Object)}.
     * This will be invoked on the same thread as
     * {@link Client#update(int)} and
     * {@link Server#update(int)}. This method should not block
     * for long periods as other network activity will not be processed until it
     * returns.
     *
     * @param connection 连接
     */
    @Override
    public final void connected(final Connection connection) {
        if (!ContextProperties.IS_ACTIVE) {
            logger.error("application context is not active, can not process request about rpc.");
            return;
        }
        // 如果有rpc服务里面要提供
        if (idToObject != null && idToObject.size() > 0) {
            // 绑定RPC服务
            if (space == null) {
                // 新建对象空间
                space = new ObjectSpace(connection);
                // 注册服务到当前空间
                space.register(idToObject);
            } else {
                space.addConnection(connection);
            }
        }
        if (listeners != null && listeners.size() > 0) {
            JobQueueOB.getInstance().addJob(new Runnable() {
                @Override
                public void run() {
                    for (RpcServiceListener listener : listeners)
                        listener.onConnected(connection);
                }
            });
        }
    }

    /**
     * Called when the remote end is no longer connected. There is no guarantee
     * as to what thread will invoke this method.
     *
     * @param connection 连接
     */
    @Override
    public final void disconnected(final Connection connection) {
        // 取消当前连接的RPC服务
        if (space != null) {
            space.removeConnection(connection);
        }
        if (listeners != null && listeners.size() > 0) {
            JobQueueOB.getInstance().addJob(new Runnable() {
                @Override
                public void run() {
                    for (RpcServiceListener listener : listeners)
                        listener.onDisconnected(connection);
                }
            });
        }
    }

    /**
     * Called when an object has been received from the remote end of the
     * connection. This will be invoked on the same thread as
     * {@link Client#update(int)} and
     * {@link Server#update(int)}. This method should not block
     * for long periods as other network activity will not be processed until it
     * returns.
     *
     * @param connection 连接
     * @param object     数据对象
     */
    @Override
    public final void received(final Connection connection, final Object object) {
        if (!ContextProperties.IS_ACTIVE) {
            logger.error("application context is not active, can not process request about rpc.");
            return;
        }
        if (listeners != null && listeners.size() > 0) {
            JobQueueOB.getInstance().addJob(new Runnable() {
                @Override
                public void run() {
                    for (RpcServiceListener listener : listeners)
                        listener.onMessageReceived(connection, object);
                }
            });
        }
    }

    /**
     * Called when the connection is below the
     * {@link com.handee.rpc.Connection#setIdleThreshold(float) idle threshold}.
     *
     * @param connection 连接
     */
    @Override
    public final void idle(final Connection connection) {
        if (listeners != null && listeners.size() > 0) {
            JobQueueOB.getInstance().addJob(new Runnable() {
                @Override
                public void run() {
                    for (RpcServiceListener listener : listeners)
                        listener.onIdle(connection);
                }
            });
        }
    }
}
