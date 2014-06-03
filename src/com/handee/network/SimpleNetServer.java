package com.handee.network;

import com.handee.job.RunnableNetJob;
import com.handee.utils.JobQueueOB;
import com.handee.utils.Utils;

import java.net.Socket;
import java.util.ArrayList;

public class SimpleNetServer extends NetServer implements SocketConsumer, Runnable {
    private SocketFactory socketFactory;
    private ArrayList<SimpleConnection> connectionList = new ArrayList<>();
    /**
     * 接受新连接的时间，默认为100，即每秒最多接受10个连接请求
     */
    private int acceptDelay = 100;

    public boolean start() {
        if (socketFactory == null) {
            try {
                socketFactory = new SocketFactory(getPort(), this);
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            }
            socketFactory.start();
            new Thread(this, "SimpleNetServer").start();
        }
        return true;
    }

    public void stop() {
        try {
            socketFactory.stop();
            socketFactory = null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public int getAcceptDelay() {
        return acceptDelay;
    }

    public void setAcceptDelay(int delay) {
        acceptDelay = delay;
    }

    public int getConnectionCount() {
        return connectionList.size();
    }

    public boolean acceptSocket() {
        return getMaxConnections() <= 0 || getMaxConnections() > getConnectionCount();
    }

    public void closeAllConnections() {
        for (SimpleConnection simpleConnection : connectionList) {
            if (simpleConnection != null && simpleConnection.isActive()) {
                simpleConnection.close();
            }
        }
        connectionList.clear();
    }

    public boolean consumeSocket(Socket socket) {
        if (!acceptSocket())
            return false;
        try {
            SimpleConnection connection = new SimpleConnection(new TcpSocket(socket));
            connectionOpened(connection);
            connectionList.add(connection);
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void run() {
        ArrayList<SimpleConnection> deletedList = new ArrayList<>();
        long start, time;
        while (socketFactory != null) {
            start = System.currentTimeMillis();

            for (SimpleConnection simpleConnection : connectionList) {
                if (simpleConnection.isActive()) {
                    JobQueueOB.getInstance().addJob(2, new ConnectionPeekJob(simpleConnection));
                } else
                    deletedList.add(simpleConnection);
            }

            for (NetConnection conn : deletedList) {
                connectionClosed(conn);
                connectionList.remove(conn);
            }
            deletedList.clear();
            time = System.currentTimeMillis() - start;
            if (time < 5)
                Utils.sleep(5);
        }
    }

    private class ConnectionPeekJob extends RunnableNetJob<Void> {
        private SimpleConnection simpleConnection;

        public ConnectionPeekJob(SimpleConnection simpleConnection) {
            this.simpleConnection = simpleConnection;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.handee.network.NetJob#process()
         */
        @Override
        protected Void process() {
            simpleConnection.processPeek(System.currentTimeMillis());
            return null;
        }
    }
}