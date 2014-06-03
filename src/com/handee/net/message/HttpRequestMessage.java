package com.handee.net.message;

import com.handee.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A HTTP request message.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007)
 *          $
 */
public class HttpRequestMessage extends NetMessage {
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestMessage.class);
    /**
     *
     */
    private static final long serialVersionUID = 541165145595563142L;
    /**
     * Map<String, String[]>
     */
    private Map<String, String[]> headers = null;
    private String remoteAddress;
    private int remotePort;

    public HttpRequestMessage(long sessionId) {
        super(null);
        setSessionId(sessionId);
    }

    public void setHeaders(Map<String, String[]> headers) {
        this.headers = headers;
    }

    public Map<String, String[]> getHeaders() {
        return headers;
    }

    public String getContext() {
        String[] context = headers.get("Context");
        return context == null ? "" : context[0];
    }

    public String getParameter(String name) {
        String[] param = headers.get("@".concat(name));
        try {
            return URLDecoder.decode(param == null ? "" : param[0], "utf-8");
        } catch (Exception e) {
            LOG.error("get parameter error.", e);
        }
        return "";
    }

    public String[] getParameters(String name) {
        String[] param = headers.get("@".concat(name));
        return param == null ? new String[]{} : param;
    }

    public String[] getHeader(String name) {
        return headers.get(name);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (Entry<String, String[]> e : headers.entrySet()) {
            str.append(e.getKey());
            str.append(" : ");
            str.append(arrayToString(e.getValue(), ','));
            str.append("\n");
        }

        return str.toString();
    }

    public String getMethod() {
        String[] slashes = getContext().split("/");
        return slashes[slashes.length - 1];
    }

    public static String arrayToString(String[] s, char sep) {
        if (s == null || s.length == 0)
            return "";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i > 0)
                buf.append(sep);
            buf.append(s[i]);
        }
        return buf.toString();
    }

    public String toJson() {
        return JSON.toJson(headers);
    }

    /**
     * @return the remoteAddress
     */
    public final String getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * @param remoteAddress the remoteAddress to set
     */
    public final void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * @return the remotePort
     */
    public final int getRemotePort() {
        return remotePort;
    }

    /**
     * @param remotePort the remotePort to set
     */
    public final void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        headers.clear();
        headers = null;
    }
}
