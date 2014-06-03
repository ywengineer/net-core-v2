/*******************************************************************
 * @Version : 0.5
 *
 *
 *          AbstractNetServer.java
 *
 *
 *          2011-10-11 上午09:16:35
 *
 *******************************************************************/
package com.handee.network;

import com.handee.event.listener.INetHandler;
import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.net.message.codec.app.AppMessageCodecFactory;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

import java.util.List;

/**
 * 网络基础服务器.
 * <p/>
 * 仅提供基础功能，并不实际提供服务。
 *
 * @author Mark
 */
public abstract class AbstractNetServer implements INetServerListener {

    protected MinaNetServer netServer;

    protected int port;

    protected MessageCodecFactory factory;

    protected INetHandler handler;

    private boolean running = false;

    public AbstractNetServer(int port) {// 默认为二进制消息
        this(port, new AppMessageCodecFactory());
    }

    public AbstractNetServer(int port, MessageCodecFactory factory) {
        this.port = port;
        this.factory = factory;
    }

    public void start() {
        if (running)
            return;
        netServer = new MinaNetServer();
        netServer.setMaxConnections(2000);
        netServer.init(port, false);
        netServer.addNetServerListener(this);
        netServer.setMessageCodecFactory(factory);
        running = netServer.start();
    }

    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        try {
            handler = null;
            netServer.removeAllNetServerListeners();
            netServer.stop(); // 停止网络服务器
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static final Logger logger = Logger.getLogger(AbstractNetServer.class);

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    protected boolean checkIP(String ip) {
        return true;
    }

    public final List<IoSession> getAllSession() {
        return netServer.getAllSession();
    }
}
