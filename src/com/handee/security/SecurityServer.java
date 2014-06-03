/*******************************************************************
 *
 * Copyright © 2009 - 2010 All Rights Reserved. 
 *
 * @Version : 1.0 
 *
 * SecurityServer.java
 *
 * @author wangyongdong
 *
 * @Eamil haya8721@gmail.com
 *
 * 2010-3-22 上午11:09:45
 *
 *******************************************************************/
package com.handee.security;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SecurityServer implements Runnable {
    private static final Logger logger = Logger.getLogger(SecurityServer.class);
    private ServerSocket server;
    private String xml;

    public SecurityServer() {
        //String path = "crossdomain.xml";
        // 此处的换成相应的读取xml文档的方式如dom或sax
        // xml = readFile(path, "UTF-8");
        /**
         * 注意此处xml文件的内容，为纯字符串，没有xml文档的版本号
         */
        xml = "<cross-domain-policy> "
                + "<site-control permitted-cross-domain-policies=\"all\"/>"
                + "<allow-access-from domain=\"*\" to-ports=\"*\"/>"
                + "</cross-domain-policy> ";
        //	System.out.println("policyfile文件路径: " + path);
        //	System.out.println(xml);

        // 启动843端口
        createServerSocket(843);
        new Thread(this).start();
    }

    // 启动服务器
    private void createServerSocket(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("安全沙箱服务器端口：" + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    // 启动服务器线程
    public void run() {
        ThreadPool threadPool = new ThreadPool(100, 1000);
        threadPool.init();
        while (true) {
            Socket client = null;
            try {
                // 接收客户端的连接
                client = server.accept();

                threadPool.addTask(new SendPolicyFile(client, xml));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                try {
                    // 发现异常关闭连接
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    //调用垃圾收集方法
                    System.gc();
                }
            }
        }
    }

    //测试主函数
    public static void main(String[] args) {
        new SecurityServer();
    }
}

class SendPolicyFile implements Runnable {
    private static final Logger logger = Logger.getLogger(SendPolicyFile.class);
    private Socket sourceSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String policyFile;

    public SendPolicyFile(Socket socket, String policyFile) {
        this.policyFile = policyFile;
        sourceSocket = socket;
    }

    public void run() {
        try {
            //先发送安全策略验证内容给客户端
            sourceSocket.setSoTimeout(3000);

            InputStreamReader input = new InputStreamReader(sourceSocket.getInputStream(), "UTF-8");
            reader = new BufferedReader(input);
            OutputStreamWriter output = new OutputStreamWriter(sourceSocket.getOutputStream(), "UTF-8");
            writer = new BufferedWriter(output);

            // 读取客户端发送的数据
            StringBuilder data = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                if (c != '\0')
                    data.append((char) c);
                else
                    break;
            }
            String info = data.toString();
            System.out.println("输入的请求: " + info);

            // 接收到客户端的请求之后，将策略文件发送出去
            if (info.contains("<policy-file-request/>")) {
                writer.write(policyFile + "\0");
                writer.flush();
                logger.info("将安全策略文件发送至: " + sourceSocket.getInetAddress());
            } else {
                writer.write("请求无法识别\0");
                writer.flush();
                logger.error("请求无法识别: " + sourceSocket.getInetAddress());
            }
            sourceSocket.close();
        } catch (Exception e) {
            try {
                // 发现异常关闭连接
                if (sourceSocket != null) {
                    sourceSocket.close();
                    sourceSocket = null;
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            } finally {
                //调用垃圾收集方法
                System.gc();
            }
        }
    }
}
