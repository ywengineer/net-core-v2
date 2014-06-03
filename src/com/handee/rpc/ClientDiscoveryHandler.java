package com.handee.rpc;

import com.esotericsoftware.kryo.Kryo;

import java.net.DatagramPacket;

public interface ClientDiscoveryHandler {

    /**
     * This implementation of the {@link com.handee.rpc.ClientDiscoveryHandler} is responsible
     * for providing the {@link com.handee.rpc.Client} with it's default behavior.
     */
    public static final ClientDiscoveryHandler DEFAULT = new ClientDiscoveryHandler() {

        @Override
        public DatagramPacket onRequestNewDatagramPacket() {
            return new DatagramPacket(new byte[0], 0);
        }

        @Override
        public void onDiscoveredHost(DatagramPacket datagramPacket, Kryo kryo) {
            //
        }

        @Override
        public void onFinally() {
            //
        }

    };

    /**
     * Implementations of this method should return a new {@link java.net.DatagramPacket}
     * that the {@link com.handee.rpc.Client} will use to fill with the incoming packet data
     * sent by the {@link com.handee.rpc.ServerDiscoveryHandler}.
     *
     * @return a new {@link java.net.DatagramPacket}
     */
    public DatagramPacket onRequestNewDatagramPacket();

    /**
     * Called when the {@link com.handee.rpc.Client} discovers a host.
     *
     * @param datagramPacket the same {@link java.net.DatagramPacket} from
     *                       {@link #onRequestNewDatagramPacket()}, after being filled with
     *                       the incoming packet data.
     * @param kryo           the {@link com.esotericsoftware.kryo.Kryo} instance
     */
    public void onDiscoveredHost(DatagramPacket datagramPacket, Kryo kryo);

    /**
     * Called right before the {@link com.handee.rpc.Client#discoverHost(int, int)} or
     * {@link com.handee.rpc.Client#discoverHosts(int, int)} method exits. This allows the
     * implementation to clean up any resources used, i.e. an {@link com.esotericsoftware.kryo.io.Input}.
     */
    public void onFinally();

}
