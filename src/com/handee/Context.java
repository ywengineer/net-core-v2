/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * Scanner.java
 *
 * 2013 2013-5-17 上午11:43:24
 *
 *******************************************************************/
package com.handee;

import com.esotericsoftware.minlog.Log;
import com.handee.annotations.Validator;
import com.handee.cluster.MessageChannel;
import com.handee.event.Event;
import com.handee.event.emitter.EventEmitter;
import com.handee.event.listener.EventListener;
import com.handee.helper.MessageHelper;
import com.handee.helper.Reference;
import com.handee.job.RunnableNetJob;
import com.handee.net.message.NetMessage;
import com.handee.net.message.codec.MessageCodecFactory;
import com.handee.scanner.AnnotationScanner;
import com.handee.scheduler.Schedule;
import com.handee.scheduler.ScheduleManager;
import com.handee.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.jgroups.Address;
import org.jgroups.Message;
import org.quartz.*;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * 上下文环境。
 *
 * @author Mark
 */
@SuppressWarnings("rawtypes")
public class Context extends EventEmitter {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Context.class);
    private static final Context scanner;
    private static final String EVENT_CONTEXT_STARTED = "ContextStarted";
    private static final String EVENT_CONTEXT_STOPED = "ContextStoped";
    private static ContextEventListener LISTENER;
    private static ResourceBundle handee;
    private static Registry rmiServices;

    private Set<Class<?>> allClassesInClasspath;

    private BaseServer socket;

    private BaseServer http;

    private Scheduler scheduler;

    private MessageChannel channel;

    private Thread runner;

    static {
        scanner = new Context();
        scanner.addListener(EVENT_CONTEXT_STOPED, new EventListener() {
            @Override
            public void on(Event event) {
                if (LISTENER != null) {
                    LISTENER.stoped();
                }
            }
        });
        scanner.addListener(EVENT_CONTEXT_STARTED, new EventListener() {
            @Override
            public void on(Event event) {
                if (LISTENER != null) {
                    LISTENER.started();
                }
            }
        });

        // 配置文件
        handee = ResourceBundle.getBundle("handee");
    }

    public static void main(String[] args) {
        scanner.start();
    }

    private Context() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        Log.set(Logger.getRootLogger().getLevel().toInt() / 10000 + 1);
    }

    public static void stop() {
        ContextProperties.IS_ACTIVE = false;
        scanner.emmit(new Event(EVENT_CONTEXT_STOPED));
        if (scanner.socket != null && scanner.socket.isRunning()) {
            scanner.socket.stop();
        }
        if (scanner.http != null && scanner.http.isRunning()) {
            scanner.http.stop();
        }
        if (scanner.channel != null) {
            scanner.channel.close();
        }
        if (scanner.runner != null) {
            scanner.runner.interrupt();
            scanner.runner = null;
        }
        scanner.removeAllListeners();
        unbindAllRemoteService();
        stopScheduler();
        JobQueueOB.getInstance().stop();
        System.exit(0);
    }

    /**
     * 停止定时服务。
     */
    public static void stopScheduler() {
        ScheduleManager.shutdownScheduler(scanner.scheduler);
        scanner.scheduler = null;
    }

    /**
     * 获取上下文参数
     *
     * @param key 参数key
     * @return 参数值
     */
    public static String getContextParameter(String key) {
        if (!handee.containsKey(key)) {
            logger.error("context key [" + key + "] not exist...");
        }
        return handee.getString(key);
    }

    /**
     * 取消所有RMI远程服务的绑定。
     * <p/>
     * 即取消所有RMI远程服务接口。
     */
    public static void unbindAllRemoteService() {
        try {
            if (rmiServices != null) {
                for (String name : rmiServices.list()) {
                    try {
                        rmiServices.unbind(name);
                    } catch (Exception e) {
                        logger.error("[Unbind Service] service ( " + name + " ) not bound.", e);
                    }
                }
                rmiServices = null;
            }
        } catch (Exception e) {
            logger.error("Unbind All Remote Service error .", e);
        }
    }

    /**
     * 在此上下文绑定远程服务。
     *
     * @param key          远程服务键
     * @param serviceClass 服务描述。
     */
    public static <T extends UnicastRemoteObject> void bindRemoteService(String key, Class<T> serviceClass) {
        try {
            if (rmiServices == null) {
                logger.error("remote service not enabled" + key + "," + serviceClass.getName());
                return;
            }
            rmiServices.rebind(key, ClassUtils.getClassInstance(serviceClass));
        } catch (Exception e) {
            logger.error("bind remote service error : " + key + "," + serviceClass.getName(), e);
        }
    }

    /**
     * 启用RMI相关服务。
     * <p/>
     * 系统默认不会主动启用RMI相关服务，如果需要RMI服务，请指定上下文监听器。
     * <p/>
     * 在上下文启动成功之后，手动调用此方法Context.enableRemoteService()。
     * <p/>
     * 不会自动绑定远程服务，需要手动设置。
     */
    public static void enableRemoteService() {
        try {
            if (ContextProperties.RMI_SERVER_ENABLE) {
                // 如果已存在绑定。取消所有服务
                unbindAllRemoteService();
                // RMI registry entrance
                rmiServices = LocateRegistry.createRegistry(ContextProperties.RMI_PORT);
                if (StringUtils.isNotEmpty(ContextProperties.IP)) {
                    logger.info("rmi server host name = " + ContextProperties.IP);
                    System.setProperty("java.rmi.server.hostname", ContextProperties.IP);
                } else if (StringUtils.isNotEmpty(ContextProperties.LOCAL_IP)) {
                    logger.info("rmi server host name = " + ContextProperties.LOCAL_IP);
                    System.setProperty("java.rmi.server.hostname", ContextProperties.LOCAL_IP);
                } else {
                    logger.error("rmi server host not enable, this service can not access from remote");
                }
                // iterate RMI interface.
                SystemUtils.printSection("RMI server started. listening on port : " + ContextProperties.RMI_PORT);
            }

            if (ContextProperties.RMI_CLIENT_ENABLE && StringUtils.isNotEmpty(ContextProperties.RMI_SERVICE_LIST)) {
                String[] list = StringUtils.split(ContextProperties.RMI_SERVICE_LIST, ",");
                for (String service : list) {
                    String[] serviceInfo = StringUtils.split(service, ":");
                    if (serviceInfo.length != 2) {
                        continue;
                    }
                    RemoteServices.getInstance().addRegistry(service, LocateRegistry.getRegistry(StringUtils.trim(serviceInfo[0]), MathUtils.getInteger(serviceInfo[1])));
                    logger.info("RMI Client connect to [host:" + serviceInfo[0] + ", port:" + serviceInfo[1] + "]");
                }
                if (RemoteServices.getInstance().serviceSize() > 0) {
                    SystemUtils.printSection("RMI Client connected..service size : " + RemoteServices.getInstance().serviceSize());
                }
            }
        } catch (RemoteException e) {
            // e.printStackTrace();
            logger.error("rmi service registry error :: ", e);
        }
    }

    /**
     * 发送一个集群消息。
     * <p/>
     * 如果是一个不需要广播的消息，但是没目的地，系统会自动选择一个目的地址。
     * <p/>
     * 选取目的地规则为：在同一个集群环境中的任一不同逻辑地址。
     *
     * @param clusterMessage 集群消息
     * @param broadcast      是否需要广播
     */
    public static void sendClusterMessage(Message clusterMessage, boolean broadcast) {
        try {
            // lock_cluster.lock();
            if (scanner.channel == null) {
                logger.error("this service not in cluster enviroment.");
                return;
            }
            // it is not a broadcast message
            if (!broadcast) {
                // do not have destination yet.
                if (clusterMessage.getDest() == null) {
                    //
                    List<Address> destAddresses = scanner.channel.getMembersOfOtherLogicalAddress();
                    if (destAddresses.size() <= 0) {
                        logger.error("no destination for this cluster message");
                        return;
                    }
                    Address dest = destAddresses.get(Math.abs(clusterMessage.hashCode() % destAddresses.size()));
                    clusterMessage.setDest(dest);
                }
            } else {
                clusterMessage.setDest(null);
            }
            scanner.channel.send(clusterMessage);
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error("send cluster message failure [sendClusterMessage(Message clusterMessage, boolean broadcast)] :: ", e);
        }
    }

    /**
     * 发送集群消息。
     * <p/>
     * 如果逻辑地址不在此集群环境中，消息将会丢弃。
     * <p/>
     * 如果需要广播，则此消息将会单播到参数指定的每个逻辑地址。
     * <p/>
     * 如果不需要广播且消息还没有指定目的地，系统会将此消息单播到任一指定的逻辑地址。
     *
     * @param clusterMessage 消息
     * @param logicalName    逻辑地址
     * @param broadcast      是否广播
     */
    public static void sendClusterMessage(Message clusterMessage, String logicalName, boolean broadcast) {
        try {
            // lock_cluster.lock();
            if (scanner.channel == null) {
                logger.error("this service not in cluster enviroment.");
                return;
            }
            // get all logical address by logical name
            List<Address> allLogicalAddress = scanner.channel.getMembersByLogicalName(logicalName);
            // no logical address exist in cluster.
            if (allLogicalAddress == null || allLogicalAddress.size() < 1) {
                logger.error("logical name [" + logicalName + "] not exsit in current cluster enviroment, message will be discard.");
                return;
            }
            // this message do not need broadcast.
            if (!broadcast) {
                // do not have destination yet.
                if (clusterMessage.getDest() == null) {
                    // random an address from all logical address.
                    Address dest = allLogicalAddress.get(Math.abs(clusterMessage.hashCode() % allLogicalAddress.size()));
                    // set destination for this message
                    clusterMessage.setDest(dest);
                }
                // send to cluster
                scanner.channel.send(clusterMessage);
            } else {// broadcast message. send message to every logical address
                for (Address address : allLogicalAddress) {
                    // set destination.
                    clusterMessage.setDest(address);
                    // send message
                    scanner.channel.send(clusterMessage);
                }
            }
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error("send cluster message failure [sendClusterMessage(Message clusterMessage, String logicalAddress, boolean broadcast)] :: ", e);
        }
    }

    /**
     * write a message to http client.
     *
     * @param message         http message
     * @param closeAfterWrite close or not close connection after write this message.
     */
    public static void writeHttpResponse(NetMessage message, boolean closeAfterWrite) {
        try {
            if (scanner.http == null) {
                logger.error("http service not start. can not write http response");
                return;
            }
            scanner.http.writeMessage(message, closeAfterWrite);
        } catch (Exception e) {
            logger.error("write a http response error :: ", e);
        }
    }

    /**
     * write a protocol message to client.
     *
     * @param message         socket message (with protocol of itself)
     * @param closeAfterWrite close or not close connection after write this message.
     */
    public static void writeSocketResponse(NetMessage message, boolean closeAfterWrite) {
        try {
            if (scanner.socket == null) {
                logger.error("socket service not start. can not write response");
                return;
            }
            scanner.socket.writeMessage(message, closeAfterWrite);
        } catch (Exception e) {
            logger.error("write a socket response message error :: ", e);
        }
    }

    private void start() {
        // all classes
        allClassesInClasspath = ClassUtils.getClasses("");
        // 解析配置参数
        parseParams();
        // 参数检验
        validateParams();
        // 初始化队列
        initQueue();
        // 扫描定时工作
        scanScheduleAndMessageJob();
        // 启动定时服务
        startScheduler();
        // 开启服务器
        startServer();
        // 连接集群机组
        connectToCluster();
        // 心跳
        startHeartBeat();
        // GC
        allClassesInClasspath.clear();
        allClassesInClasspath = null;
        ContextProperties.IS_ACTIVE = true;
        emmit(new Event(EVENT_CONTEXT_STARTED));
    }

    private void validateParams() {
        Map<String, Object> params = new HashMap<>();
        for (Field field : ContextProperties.class.getFields()) {
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers)) {
                try {
                    Object value = field.get(null);
                    Validator validator = field.getAnnotation(Validator.class);
                    if (validator == null) {
                        continue;
                    }
                    params.clear();
                    params.put(validator.placeHolder(), value);
                    boolean isValid = MathUtils.getBoolean(ExpressionUtils.eval(validator.script(), params));
                    if (!isValid) {
                        logger.error(field.getType() + " :: " + validator.failureMessage().replaceAll("defaultValue", validator.defaultValue()));
                        if (validator.failureToExit()) {
                            System.exit(0);
                        } else {
                            com.handee.utils.ClassUtils.setValue(null, field, validator.defaultValue());
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("Validate Parameters error :: ", e);// e.printStackTrace();
                }
            }
        }
    }

    private void parseParams() {
        // 上下文启动监听器
        ContextProperties.CONTEXT_EVENT_LISTENER = ResourceBundleUtils.getClass(handee, "context.event.listener");
        // 初始化监听器
        Context.LISTENER = ClassUtils.getClassInstance(ContextProperties.CONTEXT_EVENT_LISTENER);
        /** 缓冲区最大空间 */
        ContextProperties.MAX_BUFFER_SIZE = ResourceBundleUtils.getInteger(handee, "max.buffer.size");
        /** Client超时时间 */
        ContextProperties.TIME_CLIENT_TIMEOUT = ResourceBundleUtils.getInteger(handee, "time.timeout");
        /** 空闲时，检测时间间隔 */
        ContextProperties.TIME_PING = ResourceBundleUtils.getInteger(handee, "time.ping");
        /** 心跳时间，0表示不需要时间 */
        ContextProperties.TIME_HEART_BEAT = ResourceBundleUtils.getInteger(handee, "time.heart.beat");
        /** 心跳执行 */
        ContextProperties.DO_HEART_BEAT = ResourceBundleUtils.getClassInstance(handee, "do.heart.beat");
        // queue size
        ContextProperties.QUEUE_SIZE = ResourceBundleUtils.getInteger(handee, "queue.size");
        // default timeout for each queue
        ContextProperties.QUEUE_TIMEOUT_DEFAULT = ResourceBundleUtils.getInteger(handee, "queue.timeout");
        // scheduler
        ContextProperties.HAS_SCHEDULER = ResourceBundleUtils.getBoolean(handee, "scheduler");
        // listen port
        ContextProperties.SOCKET_PORT = ResourceBundleUtils.getInteger(handee, "socket.port");
        // http port
        ContextProperties.HTTP_PORT = ResourceBundleUtils.getInteger(handee, "http.port");
        // 数据编码，解码器
        ContextProperties.MESSAGE_CODEC_FACTORY = ResourceBundleUtils.getClass(handee, "socket.codec.factory");
        // 数据处理器
        ContextProperties.HANDLER = ResourceBundleUtils.getClass(handee, "server.process.handler");
        // 所以集群
        ContextProperties.CLUSTER_NAME = ResourceBundleUtils.getString(handee, "cluster.name");
        // 集群协议栈
        ContextProperties.CLUSTER_PROTOCAL_STACK = ResourceBundleUtils.getString(handee, "cluster.protocal.stack");
        // 集群节点名称
        ContextProperties.CLUSTER_NODE_NAME = ResourceBundleUtils.getString(handee, "cluster.node.name");
        // 集群消息处理器
        ContextProperties.CLUSTER_MESSAGE_HANDLER = ResourceBundleUtils.getClass(handee, "cluster.message.handler");
        // 是否启用Socket服务
        ContextProperties.SOCKET_ENABLE = ResourceBundleUtils.getBoolean(handee, "socket.enable");
        // 是否启用HTTP服务
        ContextProperties.HTTP_ENABLE = ResourceBundleUtils.getBoolean(handee, "http.enable");
        // 无效消息处理
        ContextProperties.PROCESS_FOR_ERROR_MESSAGE = ResourceBundleUtils.getClass(handee, "job.process.for.error.code");
        // RMI服务
        ContextProperties.RMI_SERVER_ENABLE = ResourceBundleUtils.getBoolean(handee, "rmi.server.enable");
        // RMI Client 服务
        ContextProperties.RMI_CLIENT_ENABLE = ResourceBundleUtils.getBoolean(handee, "rmi.client.enable");
        // RMI端口
        ContextProperties.RMI_PORT = ResourceBundleUtils.getInteger(handee, "rmi.port");
        // RMI 服务列表
        ContextProperties.RMI_SERVICE_LIST = ResourceBundleUtils.getString(handee, "rmi.service.list");
        // 本机网络地址
        ContextProperties.IP = Utils.getInternetIP();
        // 本机地址
        ContextProperties.LOCAL_IP = Utils.getLocalAddress().getHostAddress();
        // 最大连接数
        ContextProperties.MAX_CONNECTIONS = ResourceBundleUtils.getInteger(handee, "max.connections");
        // 日志
        logger.error("current computer net information :: WAN : [" + ContextProperties.IP + "], LAN : [" + ContextProperties.LOCAL_IP + "]");
    }

    private void initQueue() {
        // define size for JobQueueOB
        JobQueueOB.JOB_QUEUE_SIZE = ContextProperties.QUEUE_SIZE;
        // need queue
        if (ContextProperties.QUEUE_SIZE > 0) {
            // index of queue
            int queueIndex = 0;
            // set timeout for each queue.
            while (queueIndex < ContextProperties.QUEUE_SIZE) {
                // 设置超时
                JobQueueOB.getInstance().setQueueTimeout(queueIndex, ContextProperties.QUEUE_TIMEOUT_DEFAULT);
                // next queue
                queueIndex++;
            }
            // 初始化队列完成
            logger.info("JobQueueOB initialled");
        }
    }

    private void startServer() {
        if (ContextProperties.SOCKET_ENABLE) {
            // start nio socket service.
            ProtocolCodecFactory protocalFactory = ClassUtils.getClassInstance(ContextProperties.MESSAGE_CODEC_FACTORY);
            if (protocalFactory instanceof MessageCodecFactory) {
                socket = new CustomCodecSocketServer(ContextProperties.SOCKET_PORT, (MessageCodecFactory) protocalFactory);
            } else {
                socket = new MinaCodecSocketServer(ContextProperties.SOCKET_PORT, protocalFactory);
            }
            socket.setMaxConnections(ContextProperties.MAX_CONNECTIONS);
            socket.setHandler(ClassUtils.getClassInstance(ContextProperties.HANDLER));
            socket.start();
        }
        if (ContextProperties.HTTP_ENABLE) {
            // start http service.
            http = new HttpServer(ContextProperties.HTTP_PORT);
            http.setMaxConnections(ContextProperties.MAX_CONNECTIONS);
            http.setHandler(ClassUtils.getClassInstance(ContextProperties.HANDLER));
            http.start();
        }
    }

    private void connectToCluster() {
        try {
            if (StringUtils.isNotEmpty(ContextProperties.CLUSTER_NAME)) {
                channel = new MessageChannel();
                if (StringUtils.isNotEmpty(ContextProperties.CLUSTER_NODE_NAME)) {
                    channel.setName(ContextProperties.CLUSTER_NODE_NAME);
                }
                channel.setHandler(ClassUtils.getClassInstance(ContextProperties.CLUSTER_MESSAGE_HANDLER));
                channel.connect(ContextProperties.CLUSTER_NAME);
            }
        } catch (Exception e) {
            logger.error("connect to cluster[" + ContextProperties.CLUSTER_NAME + "] error :: ", e);
        }
    }

    private void startScheduler() {
        if (ContextProperties.HAS_SCHEDULER && scheduler != null) {
            scheduler = ScheduleManager.start(scheduler);
        } else {
            logger.error("Scheduler service is not started. cauz there have not any job need to schedule.");
        }
    }

    private void scanScheduleAndMessageJob() {
        // all schedule job.
        List<Reference<Schedule, Class<? extends Job>>> allScheduleJobs = AnnotationScanner.scanScheduleJob(allClassesInClasspath);
        // have job to schedule.
        if (allScheduleJobs.size() > 0) {
            // gain schedule service.
            scheduler = ScheduleManager.getPlatformScheduler();
            // iterator
            for (Reference<Schedule, Class<? extends Job>> reference : allScheduleJobs) {
                try {
                    // the detail of current schedule job
                    JobDetail jobDetail = JobBuilder.newJob(reference.value).build();
                    // the trigger for trigger current schedule job.
                    Trigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(reference.key.cronExpression())).build();
                    // register to schedule service
                    scheduler.scheduleJob(jobDetail, trigger);
                    // 日志
                    logger.info("schedule job name=[{}], time=[{}]", reference.value.getName(), reference.getKey().cronExpression());
                } catch (SchedulerException e) {
                    logger.error("schedule job error :: ", e);
                }
            }
        }
        /***************************************************************************************************************************************************/
        // initialize message command.
        MessageHelper.getInstance().setCommand(AnnotationScanner.scanMessageJob(allClassesInClasspath));
    }

    /**
     * 开启心跳
     */
    private void startHeartBeat() {
        if (ContextProperties.TIME_HEART_BEAT > 0) {
            runner = new Runner();
            runner.setDaemon(true);
            runner.start();
            SystemUtils.printSection("heart beat started.");
        }
    }

    private class Runner extends Thread {
        private long currentTime;

        public Runner() {
            super("Context Heart Beat Thread");
        }

        public void run() {
            Utils.sleep(10000);
            while (runner == this) {
                if (runner != this) {
                    break;
                }
                Utils.sleep(50);
                long timeMillis = System.currentTimeMillis();
                // 上下文可用且达到心跳间隔时间
                if (ContextProperties.IS_ACTIVE && timeMillis - currentTime > ContextProperties.TIME_HEART_BEAT) {
                    currentTime = timeMillis;
                    Utils.runAsyn(new DoHeartBeatJob(), null);
                }
            }
            runner = null;
        }
    }

    private class DoHeartBeatJob extends RunnableNetJob<Void> {

        /*
         * (non-Javadoc)
         *
         * @see com.handee.job.RunnableNetJob#process()
         */
        @Override
        protected Void process() {
            if (ContextProperties.DO_HEART_BEAT != null) {
                ContextProperties.DO_HEART_BEAT.run();
            }
            return null;
        }

    }
}
