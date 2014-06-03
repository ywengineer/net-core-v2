package com.handee.rpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public interface ServerDiscoveryHandler {

    /**
     * This implementation of {@link com.handee.rpc.ServerDiscoveryHandler} is responsible for
     * providing the {@link com.handee.rpc.Server} with it's default behavior.
     */
    public static final ServerDiscoveryHandler DEFAULT = new ServerDiscoveryHandler() {
        private ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

        @Override
        public boolean onDiscoverHost(UdpConnection udp,
                                      InetSocketAddress fromAddress, Serialization serialization)
                throws IOException {
            udp.datagramChannel.send(emptyBuffer, fromAddress);
            return true;
        }
    };

    /**
     * Called when the {@link com.handee.rpc.Server} receives a {@link com.handee.rpc.FrameworkMessage.DiscoverHost} packet.
     *
     * @param udp           the {@link com.handee.rpc.UdpConnection}
     * @param fromAddress   {@link java.net.InetSocketAddress} the {@link com.handee.rpc.FrameworkMessage.DiscoverHost} came from
     * @param serialization the {@link com.handee.rpc.Server}'s {@link com.handee.rpc.Serialization} instance
     * @return true if a response was sent to {@code fromAddress}, false
     * otherwise
     * @throws java.io.IOException from the use of
     *                             {@link java.nio.channels.DatagramChannel#send(java.nio.ByteBuffer, java.net.SocketAddress)}
     */
    public boolean onDiscoverHost(UdpConnection udp,
                                  InetSocketAddress fromAddress, Serialization serialization)
            throws IOException;

}
