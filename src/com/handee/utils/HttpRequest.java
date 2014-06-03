package com.handee.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * HTTP请求对象
 */
public class HttpRequest {

    /**
     * 默认字符编码
     */
    private String defaultContentEncoding;

    public HttpRequest() {
        // 得到系统默认的字符编码
        this.defaultContentEncoding = "UTF-8";// Charset.defaultCharset().name();
    }

    /**
     * 　 发送GET请求
     *
     * @param urlString URL地址
     * @return 响应对象 　　 * @throws IOException
     */
    public HttpResponse sendGet(String urlString) throws IOException {
        return this.send(urlString, "GET", null, null);
    }

    /**
     * 　　 发送GET请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @return 响应对象
     * @throws IOException
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params) throws IOException {
        return this.send(urlString, "GET", params, null);
    }

    /**
     * 发送GET请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @param propertys 请求属性
     * @return 响应对象
     * @throws IOException
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params, Map<String, String> propertys) throws IOException {
        return this.send(urlString, "GET", params, propertys);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @return 响应对象
     * @throws IOException
     */
    public HttpResponse sendPost(String urlString) throws IOException {
        return this.send(urlString, "POST", null, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @return 响应对象
     * @throws IOException
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params) throws IOException {
        return this.send(urlString, "POST", params, null);
    }

    /**
     * 发送POST请求
     *
     * @param urlString URL地址
     * @param params    参数集合
     * @param propertys 请求属性
     * @return 响应对象
     * @throws IOException
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params, Map<String, String> propertys) throws IOException {
        return this.send(urlString, "POST", params, propertys);
    }

    /**
     * 发送HTTP请求
     *
     * @param urlString  请求URL
     * @param method     请求方式(get or put)
     * @param parameters 参数
     * @param properties 请求头
     * @return 响应
     * @throws IOException IO异常
     */
    private HttpResponse send(String urlString, String method, Map<String, String> parameters, Map<String, String> properties) throws IOException {
        // HttpURLConnection为局部变量
        HttpURLConnection urlConnection;
        // URL对象
        URL url;

        // 如果请求为GET方法，并且参数不为空
        if (method.equalsIgnoreCase("GET") && parameters != null) {
            // 构建并拼接参数字符串
            List<String> param = new ArrayList<>();
            for (String key : parameters.keySet()) {
                param.add(URLEncoder.encode(key, defaultContentEncoding) + "=" + URLEncoder.encode(parameters.get(key), defaultContentEncoding));
            }

            // 拼接URL串 + 参数
            urlString += StringUtils.join(param, "&");
        }
        // NEW一个URL对象，由该对象的openConnection()方法将生成一个URLConnection对象
        url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();

        // 设置相关属性，具体含义请查阅JDK文档
        urlConnection.setRequestMethod(method);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        // 赋予请求属性
        if (properties != null) {
            for (String key : properties.keySet()) {
                urlConnection.addRequestProperty(key, properties.get(key));
            }
        }

        // 如果请求为POST方法，并且参数不为空
        if (method.equalsIgnoreCase("POST") && parameters != null) {
            List<String> param = new ArrayList<>();
            for (String key : parameters.keySet()) {
                param.add(URLEncoder.encode(key, defaultContentEncoding) + "=" + URLEncoder.encode(parameters.get(key), defaultContentEncoding));
            }

            String postData = StringUtils.join(param, "&");
            // 将参数信息发送到HTTP服务器
            // 要注意：一旦使用了urlConnection.getOutputStream().write()方法，urlConnection.setRequestMethod("GET");将失效，其请求方法会自动转为POST
            urlConnection.getOutputStream().write(postData.getBytes(this.defaultContentEncoding));
            urlConnection.getOutputStream().flush();
            urlConnection.getOutputStream().close();
        }

        return this.makeContent(urlString, urlConnection);
    }

    /**
     * 得到响应对象
     *
     * @param urlString     请求URL地址
     * @param urlConnection HTTP连接
     * @return 响应
     * @throws IOException IO异常
     */
    private HttpResponse makeContent(String urlString, HttpURLConnection urlConnection) throws IOException {
        HttpResponse httpResponser = new HttpResponse();
        ByteArrayOutputStream output = null;
        InputStream in = null;
        try {
            // 得到响应流
            in = urlConnection.getInputStream();

            // 内容集合(集合项为行内容)
            httpResponser.contentCollection = new Vector<>();

            output = new ByteArrayOutputStream();
            int read = in.read();
            while (read != -1) {
                output.write(read);
                read = in.read();
            }

            // 得到请求连接的字符集
            String ecod = urlConnection.getContentEncoding();
            if (ecod == null)
                ecod = this.defaultContentEncoding;

            // 将各属性赋值给响应对象
            httpResponser.urlString = urlString;
            httpResponser.defaultPort = urlConnection.getURL().getDefaultPort();
            httpResponser.file = urlConnection.getURL().getFile();
            httpResponser.host = urlConnection.getURL().getHost();
            httpResponser.path = urlConnection.getURL().getPath();
            httpResponser.port = urlConnection.getURL().getPort();
            httpResponser.protocol = urlConnection.getURL().getProtocol();
            httpResponser.query = urlConnection.getURL().getQuery();
            httpResponser.ref = urlConnection.getURL().getRef();
            httpResponser.userInfo = urlConnection.getURL().getUserInfo();
            httpResponser.content = new String(output.toByteArray(), ecod);
            httpResponser.contentEncoding = ecod;
            httpResponser.code = urlConnection.getResponseCode();
            httpResponser.message = urlConnection.getResponseMessage();
            httpResponser.contentType = urlConnection.getContentType();
            httpResponser.method = urlConnection.getRequestMethod();
            httpResponser.connectTimeout = urlConnection.getConnectTimeout();
            httpResponser.readTimeout = urlConnection.getReadTimeout();

            return httpResponser;
        } catch (IOException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            if (output != null) {
                output.close();
            }
            if (in != null) {
                in.close();
            }
            // 最终关闭流
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    /**
     * 默认的响应字符集
     */
    public String getDefaultContentEncoding() {
        return this.defaultContentEncoding;
    }

    /**
     * 设置默认的响应字符集
     */
    public void setDefaultContentEncoding(String defaultContentEncoding) {
        this.defaultContentEncoding = defaultContentEncoding;
    }
}
