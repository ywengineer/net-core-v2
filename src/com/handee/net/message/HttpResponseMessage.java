package com.handee.net.message;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A HTTP response message.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007)
 *          $
 */
public class HttpResponseMessage extends NetMessage {
    private static final long serialVersionUID = -6658566046083508163L;
    public static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
    /**
     * HTTP response codes
     */
    public static final int HTTP_STATUS_SUCCESS = 200;
    public static final int HTTP_STATUS_FORBIDDEN = 403;
    public static final int HTTP_STATUS_NOT_FOUND = 404;

    /**
     * Map<String, String>
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * Storage for body of HTTP response.
     */
    private StringBuffer body = new StringBuffer();

    private int responseCode = HTTP_STATUS_SUCCESS;

    public HttpResponseMessage() {
        super(null);
        headers.put("Server", "HttpServer 0.1");
        headers.put("Cache-Control", "private");
        headers.put("Content-Type", CONTENT_TYPE_HTML);
        headers.put("Connection", "keep-alive");
        headers.put("Keep-Alive", "200");
        headers.put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
        headers.put("Last-Modified", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public HttpResponseMessage appendBody(String s) {
        body.append(s);
        return this;
    }

    public byte[] getBody() {
        return body.toString().getBytes(Charset.forName("UTF-8"));
    }
}
