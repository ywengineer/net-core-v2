package com.handee.network;

import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageDecoder;
import com.handee.net.message.codec.MessageEncoder;
import com.handee.utils.ByteBuffer;
import com.handee.utils.Utils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 网络连接器。
 *
 * @author Mark
 */
public abstract class NetConnection {
    protected Logger _log = Logger.getLogger(this.getClass());
    /**
     * An id generator guaranteed to generate unique IDs for the session
     */
    protected static AtomicLong idGenerator = new AtomicLong(0);

    public static class ConnectionInfo {
        private String ip;
        private int remotePort;
        private int localPort;

        public ConnectionInfo(String remoteAddress, int remotePort, int localPort) {
            this.ip = remoteAddress;
            this.remotePort = remotePort;
            this.localPort = localPort;
        }

        public String getRemoteAddress() {
            return ip;
        }

        public int getRemotePort() {
            return remotePort;
        }

        public int getLocalPort() {
            return localPort;
        }

        public String toString() {
            return "ip[" + ip + "] port[" + remotePort + "]";
        }
    }

    private ArrayList<INetMessageListener> listeners = new ArrayList<>();
    // private Object attachment;
    private Map<String, Object> attributes = new HashMap<>();
    private MessageDecoder messageDecoder;
    private MessageEncoder messageEncdoer;
    private MessageQueue messageQueue;
    private ByteBuffer readBuffer = new ByteBuffer(512);
    private int maxBufferSize = Integer.MAX_VALUE;
    protected long idleTime;
    protected int pingTime = 0;
    protected int timeout = 0;
    private int hungTime = 0;
    protected ConnectionInfo info;
    /**
     * The session ID
     */
    protected long sessionId;

    public void addListener(INetMessageListener l) {
        listeners.add(l);
    }

    public void removeListener(INetMessageListener l) {
        listeners.remove(l);
    }

    public void setListener(INetMessageListener l) {
        listeners.clear();
        listeners.add(l);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public INetMessageListener[] getAllListeners() {
        INetMessageListener[] array = new INetMessageListener[listeners.size()];
        listeners.toArray(array);
        return array;
    }

    public int getHungTime() {
        return hungTime;
    }

    public void setHungTime(int hungTime) {
        this.hungTime = hungTime;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.putAll(attributes);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public void clearAttributes() {
        attributes.clear();
    }

    //
    // public void setAttachment(Object o) {
    // attachment = o;
    // }
    //
    // public Object getAttachment() {
    // return attachment;
    // }

    public MessageDecoder getMessageDecoder() {
        return messageDecoder;
    }

    public void setMessageDecoder(MessageDecoder decoder) {
        messageDecoder = decoder;
    }

    public MessageEncoder getMessageEncoder() {
        return messageEncdoer;
    }

    public void setMessageEncoder(MessageEncoder encoder) {
        messageEncdoer = encoder;
    }

    public void setMessageQueue(MessageQueue queue) {
        messageQueue = queue;
    }

    public MessageQueue getMessageQueue() {
        return messageQueue;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int time) {
        timeout = time;
    }

    public int getPingTime() {
        return pingTime;
    }

    public void setPingTime(int time) {
        pingTime = time;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public void setMaxBufferSize(int size) {
        maxBufferSize = size;
    }

    public ConnectionInfo getConnectionInfo() {
        return info;
    }

    public void sendMessage(NetMessage msg) {
        if (messageEncdoer == null)
            return;
        ByteBuffer sendBuffer = messageEncdoer.encode(msg);
        sendData(sendBuffer.getRawBytes(), 0, sendBuffer.length());
    }

    public void sendData(byte[] data) {
        sendData(data, 0, data.length);
    }

    public void sendData(byte[] data, int offset, int count) {
        // int ubound = offset + count;
        // for (int i = offset; i < ubound; i++)
        // {
        // data[i] ^= code;
        // }
        sendDataImpl(data, offset, count);
        // for (int i = offset; i < ubound; i++)
        // {
        // data[i] ^= code;
        // }
    }

    /**
     * 给此连接发送消息。
     *
     * @param data   消息字节数组
     * @param offset 偏移量
     * @param count  长度
     */
    protected abstract void sendDataImpl(byte[] data, int offset, int count);

    /**
     * 关闭此连接
     */
    public abstract void close();

    /**
     * 此连接是否正常。
     *
     * @return 连接是否正常
     */
    public abstract boolean isActive();

    public void idle(long currentTime) {
        Utils.sleep(5);
        if (idleTime == 0) {
            idleTime = currentTime;
        } else {
            if (pingTime > 0 && currentTime - idleTime > pingTime) {
                sendMessage(null);
            }
            if (timeout > 0 && idleTime > 0 && currentTime - idleTime > timeout) {
                close();
            }
        }
    }

    protected void onDataRead(byte[] data, int offset, int count) {
        // int ubound = offset + count;
        // for (int i = offset; i < ubound; i++)
        // {
        // data[i] ^= code;
        // }
        idleTime = 0;
        if (messageDecoder != null) {
            readBuffer.writeBytes(data, offset, count);
            NetMessage msg = messageDecoder.decode(readBuffer);
            while (msg != null) {
                msg.setSessionId(getSessionId());
                if (messageQueue != null) {
                    messageQueue.post(msg);
                } else {
                    dispatchMessage(msg);
                }
                msg = messageDecoder.decode(readBuffer);
            }
            readBuffer.pack();
        }
        // 当前的缓存数据大于所指定值，客户有乱发数据的嫌疑，则关闭
        if (readBuffer.available() > maxBufferSize) {
            _log.error("buffer size is not enough[" + maxBufferSize + "], this data length is :: " + readBuffer.available());
            readBuffer.clear();
            close();
        }
    }

    public void dispatchMessage(NetMessage message) {
        for (INetMessageListener l : listeners) {
            l.messageArrived(this, message);
        }

    }

    public long getSessionId() {
        return this.sessionId;
    }
}