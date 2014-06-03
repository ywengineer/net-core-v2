/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * Handee.java
 * 
 * 2013 2013-5-17 下午4:20:38
 * 
 *******************************************************************/
package com.handee;

import com.handee.annotations.Validator;
import com.handee.cluster.IClusterMessageHandler;
import com.handee.event.listener.INetHandler;
import com.handee.job.RunnableNetJob;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public final class ContextProperties {
	/** 队列大小 */
	@Validator(defaultValue = "0", script = "value>0", failureMessage = "队列大小为0，没任何队列启动", placeHolder = "value")
	public static int QUEUE_SIZE = 0;
	/** 默认队列超时时间 */
	@Validator(defaultValue = "100", script = "value>0", failureMessage = "队列超时时间不能小于或者等于0，默认为defaultValue", placeHolder = "value")
	public static int QUEUE_TIMEOUT_DEFAULT = 100;
	/** 缓冲区最大空间 */
	@Validator(defaultValue = "1024", script = "value>0", failureMessage = "缓冲区最大空间不能小于或者等于0，默认为defaultValue", placeHolder = "value")
	public static int MAX_BUFFER_SIZE = 1024;
	/** Client超时时间 */
	@Validator(defaultValue = "0", script = "value>=0", failureMessage = "Client超时时间不能小于0，默认为defaultValue", placeHolder = "value")
	public static int TIME_CLIENT_TIMEOUT = 0;
	/** 空闲时，检测时间间隔 */
	@Validator(defaultValue = "0", script = "value>=0", failureMessage = "Idle检测时间间隔不能小于0，默认为defaultValue", placeHolder = "value")
	public static int TIME_PING = 0;
	/** 心跳时间，0表示不需要心跳 */
	@Validator(defaultValue = "0", script = "value>0", failureMessage = "服务器无心跳", placeHolder = "value")
	public static int TIME_HEART_BEAT = 0;
	/** 心跳执行 */
	@Validator(defaultValue = "null", script = "value!=null && value instanceof java.lang.Runnable", failureMessage = "服务器无心跳执行器", placeHolder = "value")
	public static Runnable DO_HEART_BEAT;
	/** 是否需要定时器 */
	@Validator(defaultValue = "false", script = "value == true", failureMessage = "定时服务未启动", placeHolder = "value")
	public static boolean HAS_SCHEDULER = false;
	/** Scoket服务器监听端口 */
	@Validator(defaultValue = "8788", script = "value > 0", failureMessage = "默认Socket端口为defaultValue", placeHolder = "value")
	public static int SOCKET_PORT = 80;
	/** Http服务器监听端口 */
	@Validator(defaultValue = "80", script = "value > 0", failureMessage = "默认Http端口为defaultValue", placeHolder = "value")
	public static int HTTP_PORT = 80;
	/** 消息编码，解码工厂 */
	@Validator(defaultValue = "", script = "(!com.handee.ContextProperties.SOCKET_ENABLE && !com.handee.ContextProperties.HTTP_ENABLE) || (value!=null && org.apache.mina.filter.codec.ProtocolCodecFactory.class.isAssignableFrom(value))", failureMessage = "未指定协议编解码器工厂", placeHolder = "value")
	public static Class<ProtocolCodecFactory> MESSAGE_CODEC_FACTORY;
	/** 消息和连接处理器 */
	@Validator(defaultValue = "com.handee.EmptyHandler", script = "(!com.handee.ContextProperties.SOCKET_ENABLE && !com.handee.ContextProperties.HTTP_ENABLE) || (value!=null && com.handee.event.listener.INetHandler.class.isAssignableFrom(value))", failureMessage = "未指定数据和连接处理器.", placeHolder = "value")
	public static Class<INetHandler> HANDLER;
	/** 该服务所在集群 */
	@Validator(defaultValue = "", script = "value!=null && value.length() > 0", failureMessage = "该服务不会处于任何集群服务组.", placeHolder = "value")
	public static String CLUSTER_NAME;
	/** 集群协议栈类型 */
	@Validator(defaultValue = "UDP", script = "value!=null && value.length() > 0", failureMessage = "集群协议栈默认为UDP.", placeHolder = "value")
	public static String CLUSTER_PROTOCAL_STACK = "";
	/** 集群节点名称 */
	@Validator(defaultValue = "", script = "value!=null && value.length() > 0", failureMessage = "未指定集群节点名称.", placeHolder = "value")
	public static String CLUSTER_NODE_NAME = "";
	/** 集群消息处理器 */
	@Validator(defaultValue = "", script = "value!=null && com.handee.cluster.IClusterMessageHandler.class.isAssignableFrom(value)", failureMessage = "未指定集群消息处理器.", placeHolder = "value")
	public static Class<IClusterMessageHandler> CLUSTER_MESSAGE_HANDLER = null;
	/** 是否启用Socket服务 */
	@Validator(defaultValue = "false", script = "value==true", failureMessage = "Socket服务未启用.", placeHolder = "value")
	public static boolean SOCKET_ENABLE = false;
	/** 是否启用HTTP服务 */
	@Validator(defaultValue = "false", script = "value==true", failureMessage = "HTTP服务未启用.", placeHolder = "value")
	public static boolean HTTP_ENABLE = false;
	/** 上下文启动监听器 */
	@Validator(defaultValue = "", script = "value!=null && com.handee.ContextEventListener.class.isAssignableFrom(value)", failureMessage = "无上下文启动事件监听器", placeHolder = "value")
	public static Class<ContextEventListener> CONTEXT_EVENT_LISTENER;
	/** 找不到消息协议指定处理JOB时的处理 */
	@Validator(defaultValue = "", script = "(!com.handee.ContextProperties.SOCKET_ENABLE && !com.handee.ContextProperties.HTTP_ENABLE) || (value!=null && (com.handee.job.RunnableNetJob.class.isAssignableFrom(value) || com.handee.job.CallableNetJob.class.isAssignableFrom(value)))", failureMessage = "未指定无效消息处理JOB", placeHolder = "value", failureToExit = true)
	public static Class<RunnableNetJob<?>> PROCESS_FOR_ERROR_MESSAGE;
	/** RMI服务是否启用 */
	@Validator(defaultValue = "false", script = "value==true", failureMessage = "RMI服务未启用", placeHolder = "value")
	public static boolean RMI_SERVER_ENABLE = false;
	/** RMI Client服务是否启用 */
	@Validator(defaultValue = "false", script = "value==true", failureMessage = "RMI Client服务未启用", placeHolder = "value")
	public static boolean RMI_CLIENT_ENABLE = false;
	/** RMI注册端口 */
	@Validator(defaultValue = "1039", script = "value > 0", failureMessage = "默认RMI注册端口为defaultValue", placeHolder = "value")
	public static int RMI_PORT = 1039;
	/** RMI服务列表 */
	@Validator(defaultValue = "", script = "!com.handee.ContextProperties.RMI_CLIENT_ENABLE || value.length() > 0", failureMessage = "未指定RMI Client连接的服务列表", placeHolder = "value")
	public static String RMI_SERVICE_LIST = "";
	/** 最大连接数 */
	@Validator(defaultValue = "2000", script = "com.handee.ContextProperties.SOCKET_ENABLE && value > 0", failureMessage = "未指定最大连接数，默认为:defaultValue", placeHolder = "value")
	public static int MAX_CONNECTIONS = 2000;
	/** 本地网络地址 */
	public static String IP;
	/** 本地局域网地址 */
	public static String LOCAL_IP;
	/** 上下文是否激活 */
	public static boolean IS_ACTIVE = false;
}
