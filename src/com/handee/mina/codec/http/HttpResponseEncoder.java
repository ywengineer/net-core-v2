package com.handee.mina.codec.http;

import com.handee.net.message.HttpResponseMessage;
import com.handee.net.message.NetMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

/**
 * HTTP响应编码器。
 *
 * @author Mark
 */
public class HttpResponseEncoder implements MessageEncoder<NetMessage> {
    private static final Set<Class<?>> TYPES;

    static {
        Set<Class<?>> types = new HashSet<>();
        types.add(HttpResponseMessage.class);
        TYPES = Collections.unmodifiableSet(types);
    }

    private static final byte[] CRLF = new byte[]{0x0D, 0x0A};

    public HttpResponseEncoder() {
    }

    public void encode(IoSession session, NetMessage message, ProtocolEncoderOutput out) throws Exception {
        HttpResponseMessage msg = (HttpResponseMessage) message;
        IoBuffer msgBody = IoBuffer.wrap(msg.getBody());
        IoBuffer buf = IoBuffer.allocate(256);
        // Enable auto-expand for easier encoding
        buf.setAutoExpand(true);

        try {
            // output all headers except the content length
            CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
            buf.putString("HTTP/1.1 ", encoder);
            buf.putString(String.valueOf(msg.getResponseCode()), encoder);
            switch (msg.getResponseCode()) {
                case HttpResponseMessage.HTTP_STATUS_SUCCESS:
                    buf.putString(" OK", encoder);
                    break;
                case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
                    buf.putString(" Not Found", encoder);
                    break;
            }
            buf.put(CRLF);
            for (Entry<String, String> entry : msg.getHeaders().entrySet()) {
                buf.putString(entry.getKey(), encoder);
                buf.putString(": ", encoder);
                buf.putString(entry.getValue(), encoder);
                buf.put(CRLF);
            }
            // now the content length is the body length
            buf.putString("Content-Length: ", encoder);
            buf.putString(String.valueOf(msgBody.remaining()), encoder);
            buf.put(CRLF);
            buf.put(CRLF);
            // add body
            buf.put(msgBody);
            // System.out.println("\n+++++++");
            // for (int i=0; i<buf.position();i++)System.out.print(new
            // String(new byte[]{buf.get(i)}));
            // System.out.println("\n+++++++");
        } catch (
                CharacterCodingException ex
                )

        {
            ex.printStackTrace();
        }

        buf.flip();
        out.write(buf);
    }

    public Set<Class<?>> getMessageTypes() {
        return TYPES;
    }
}
