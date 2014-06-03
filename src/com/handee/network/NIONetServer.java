package com.handee.network;

import com.handee.utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class NIONetServer extends NetServer implements Runnable {

    public NIONetServer() {
        data = new byte[1024];
        buffer = ByteBuffer.wrap(data, 0, data.length);
        connectionList = new ArrayList<>(1000);
    }

    public int getConnectionCount() {
        return connectionList.size();
    }

    public void run() {
        running = true;
        int result = 0;
        while (running) {
            long currentTime = System.currentTimeMillis();
            int size = connectionList.size();
            for (int i = 0; i < size; i++) {
                NIOConnection nioconnection = connectionList.get(i);
                if (nioconnection.isActive()) {
                    nioconnection.idle(currentTime);
                } else {
                    connectionClosed(nioconnection);
                    connectionList.remove(i--);
                    size--;
                }
            }

            try {
                result = selector.selectNow();
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
            if (result > 0)
                processSelection(currentTime);
            Utils.sleep(5);
        }
        for (NetConnection netconnection : connectionList) {
            connectionClosed(netconnection);
            netconnection.close();
        }
    }

    public boolean start() {
        boolean flag = bindServer();
        if (!flag) {
            return false;
        } else {
            (new Thread(this, "NIONetServerThread")).start();
            return true;
        }
    }

    public void stop() {
        running = false;
    }

    private boolean bindServer() {
        try {
            serverChannel = ServerSocketChannel.open();
            serverSocket = serverChannel.socket();
            serverSocket.setReceiveBufferSize(1024);
            serverSocket.setPerformancePreferences(0, 2, 1);
            serverSocket.bind(new InetSocketAddress(getPort()));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverKey = serverChannel.register(selector, 16);
            return true;
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
        return false;
    }

    private void processSelection(long currentTime) {
        currentTime--;
        Set<SelectionKey> set = selector.selectedKeys();
        Iterator<SelectionKey> iterator = set.iterator();
        while (true) {
            if (!iterator.hasNext())
                break;
            SelectionKey selectionkey = iterator.next();
            iterator.remove();
            if (selectionkey == serverKey) {
                if (selectionkey.isAcceptable()) {
                    try {
                        SocketChannel socketchannel = serverChannel.accept();
                        socketchannel.configureBlocking(false);
                        socketchannel.socket().setTcpNoDelay(false);
                        SelectionKey selectionKey = socketchannel.register(selector, 1);
                        NIOConnection nioconnection = new NIOConnection(socketchannel, this);
//                		nioconnection.setCreatedTime(System.currentTimeMillis());
                        selectionKey.attach(nioconnection);
                        connectionList.add(nioconnection);
                        connectionOpened(nioconnection);
                    } catch (IOException ioexception) {
                        ioexception.printStackTrace();
                    }
                }
            } else if (!selectionkey.isValid()) {
                NIOConnection nioconnection = (NIOConnection) selectionkey.attachment();
                if (nioconnection != null)
                    nioconnection.onClosed();
            } else if (selectionkey.isReadable()) {
                NIOConnection nioconnection = (NIOConnection) selectionkey.attachment();
                try {
                    SocketChannel socketchannel = (SocketChannel) selectionkey.channel();
                    buffer.clear();
                    int i = socketchannel.read(buffer);
                    if (i > 0) {
                        nioconnection.onDataRead(data, 0, i);
                    }
                } catch (IOException ex) {
                    nioconnection.onClosed();
                    selectionkey.cancel();
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private static final int MAX_READ_BUFFER_SIZE = 1024;
    private ServerSocketChannel serverChannel;
    private ServerSocket serverSocket;
    private Selector selector;
    private SelectionKey serverKey;
    private boolean running;
    private byte data[];
    private ByteBuffer buffer;
    private ArrayList<NIOConnection> connectionList;
}
