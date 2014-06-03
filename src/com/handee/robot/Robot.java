package com.handee.robot;

import com.handee.net.message.AppMessage;
import com.handee.net.message.HttpRequestMessage;
import com.handee.net.message.codec.app.AppMessageDecoder;
import com.handee.net.message.codec.app.AppMessageEncoder;
import com.handee.network.AbstractNetClient;
import com.handee.network.NetConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.session.IoSession;

import java.io.IOException;

public class Robot extends AbstractNetClient {
    /**
     * 用户名
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 玩家操作
     */
    private ActManager actManager;
    /**
     * 是否登录
     */
    private boolean isLogin = false;
    /**
     * 时间限制,开始作战地图 ,移动到指定地图,打开背包,聊天
     */
    long limit_mapStart = 0, limit_mapInto = 0, openBackbox = System.currentTimeMillis(), chat = 0;

    /**
     * 构造
     *
     * @param account  账号
     * @param password 密码
     * @throws IOException IO异常
     */
    public Robot(String account, String password) throws IOException {
        super(RobotConnectionConfig.HOST, RobotConnectionConfig.PORT, new AppMessageDecoder(), new AppMessageEncoder());
        this.account = account;
        this.password = password;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getAccount() {
        return account;
    }

    public void setActManager(ActManager actManager) {
        this.actManager = actManager;
    }

    /**
     * 时间流逝
     */
    public void update() {
        if (!RobotConnectionConfig.running || !isLogin)
            return;
        // TODO:Logic
    }

    /**
     * 消息到达处理
     *
     * @param arg0 网络连接
     * @param msg  消息
     */
    @Override
    public void messageArrivedImpl(NetConnection arg0, AppMessage msg) {
        switch (msg.getType()) {
            case 20000:
                isLogin = true;
                break;
            default:
                break;
        }
    }

    /**
     * 第一次连接登录
     */
    @Override
    public void onFisrtConncetion() {
        // actManager.loginByAuthPwd(account, password);
        if (StringUtils.isEmpty(password)) {
            // udid
            actManager.SendDeviceSigninMessage(account);
        } else {
            // password
            actManager.sendPassportSigninMessage(account, password);
        }
    }

    @Override
    public void onConnection() {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.handee.network.INetMessageListener#httpMessageArrived(org.apache.
     * mina.core.session.IoSession, com.handee.net.message.HttpRequestMessage)
     */
    @Override
    public void httpMessageArrived(IoSession session, HttpRequestMessage message) {

    }
}
