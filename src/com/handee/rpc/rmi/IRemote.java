/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * IRemote.java
 *
 * 14-4-21 下午12:56
 *
 *******************************************************************/
package com.handee.rpc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * com.handee.rpc.rmi.IRemote.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-4-21 下午12:56
 */
public interface IRemote extends Remote {
    /**
     * 测试连接是否正常。
     *
     * @return
     * @throws RemoteException
     */
    int ping() throws RemoteException;
}
