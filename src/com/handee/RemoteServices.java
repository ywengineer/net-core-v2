/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * com.handee.rmi.RemoteServices.java
 *
 * 2013-6-21 下午1:09:19
 *
 *******************************************************************/
package com.handee;

import com.handee.rpc.rmi.IRemote;
import com.handee.utils.MathUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 远程连接服务客户端管理类。
 *
 * @author Mark
 */
final class RemoteServices {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RemoteServices.class);
    private static final RemoteServices INSTANCE = new RemoteServices();
    private static final Map<String, IRemote> SERVICES = new HashMap<>();
    private Map<String, Registry> registries = new HashMap<>();

    private RemoteServices() {

    }

    public static RemoteServices getInstance() {
        return INSTANCE;
    }

    /**
     *  获取服务提供数量
     *
     * @return 服务数量
     */
    public int serviceSize() {
        return registries.size();
    }

    /**
     * 是否有RMI服务可用。
     *
     * @return boolean
     */
    public boolean isEnable() {
        return serviceSize() > 0;
    }

    /**
     * 设置所有RMI服务端。
     *
     * @param host     主机信息
     * @param registry RMI服务提供者
     */
    public void addRegistry(String host, Registry registry) {
        this.registries.put(host, registry);// addAll(Arrays.asList(registries));
    }

    /**
     * 获取所有服务提供者
     *
     * @return 所有服务提供者列表
     */
    public Collection<Registry> getAll() {
        return Collections.unmodifiableCollection(registries.values());
    }

    /**
     * 获取服务.
     * <p/>
     * 双重保险获取。
     * <p/>
     * 首先查看是否有缓存连接（避免重复连接），如果有则测试是否连接正常。
     * <p/>
     * 如果连接不正常，则会从远程主朵获取服务并返回且重新添加或者替换缓存连接。
     *
     * @param cls  服务接口描述符
     * @param host 服务提供主机
     * @return 服务
     */
    @SuppressWarnings("unchecked")
    public <T extends IRemote> T getService(String host, Class<T> cls) {
        String key = cls.getSimpleName();
        T service = null;
        try {
            // 如果存在缓存
            if (SERVICES.containsKey(key)) {
                // 返回缓存
                service = (T) SERVICES.get(key);
            } else {
                // 获取远程服务
                service = getServiceFromRemote(host, cls);
                // 添加缓存
                SERVICES.put(key, service);
            }

            try {
                // 测试连接是否正常
                service.ping();
            } catch (RemoteException e) {// 连接不正常时，重试重新连接
                e.printStackTrace();
                // 获取远程服务
                service = getServiceFromRemote(host, cls);
                // 添加缓存
                SERVICES.put(key, service);
            }
        } catch (RemoteException e) {
//            e.printStackTrace();
            logger.error("get service[{" + key + "}] from host[{" + host + "}] failure.", e);
        }
        //返回
        return service;
    }

    /**
     * 从远程获取服务接口.
     * <p/>
     * 双重保险，如果从当前缓存的连接获取服务失败。则会重新与主机建立连接。
     * <p/>
     * 如果重新与主机连接连接失败，则获取服务失败。如果成功则会查找服务.
     *
     * @param host 远程服务提供主机，包括IP:端口号。
     * @param cls  远程服务接口描述符
     * @param <T>  远程服务接口定义
     * @return 远程服务
     */
    private <T extends IRemote> T getServiceFromRemote(String host, Class<T> cls) throws RemoteException {
        try {
            // 查找缓存
            Registry registry = registries.get(host);
            // 如果存在缓存连接
            if (registry != null) {
                // 查找服务
                return (T) registry.lookup(cls.getSimpleName());
            }
        } catch (NotBoundException e) {
            // 查找服务失败
            logger.error("get service [" + cls.getName() + "] from remote error :: not bound ", e);
        } catch (AccessException e) {
            // 查找服务失败
            logger.error("get service [" + cls.getName() + "] from remote error :: has no permission ", e);
        } catch (Exception e) {
            String[] hostInfo = StringUtils.split(host, ":");
            // 重新绑定主机
            Registry registry = LocateRegistry.getRegistry(hostInfo[0], MathUtils.getInteger(hostInfo[1]));
            // 添加连接
            addRegistry(host, registry);
            // 查找服务
            try {
                if (registry != null) {
                    return (T) registry.lookup(cls.getSimpleName());
                }
            } catch (NotBoundException | AccessException e1) {
                logger.error("get service [" + cls.getName() + "] from remote error :: not bound or has no permission ", e1);
            }
        }
        return null;
    }
}