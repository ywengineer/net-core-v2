package com.handee.utils;

import com.github.jmkgreen.morphia.utils.ReflectionUtils;
import com.handee.event.JobProcessEvent;
import com.handee.event.listener.EventListener;
import com.handee.helper.MessageHelper.TestTime;
import com.handee.job.CallableNetJob;
import com.handee.job.JobProcessCallback;
import com.handee.job.RunnableNetJob;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableFuture;

public final class Utils {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Utils.class);
    private static final java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private static final java.text.SimpleDateFormat dayDateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
    private static final java.util.Date date = new java.util.Date();
    private static final java.util.Date nowDate = new java.util.Date();
    /**
     * 一级分隔符 :(冒号)
     */
    public static final String level1split = ":";

    /**
     * 二级分隔符 |(竖线)
     */
    public static final String level2split = "\\|";

    /**
     * 三级分隔符 ;(分号)
     */
    public static final String level3split = ";";
    /**
     * 子项分隔符 ,(逗号)
     */
    public static final String level4split = ",";
    /**
     * 子项内容分隔符 -(横线)
     */
    public static final String level0split = "-";
    public static final int JAVA_VERSION;

    static {
        JAVA_VERSION = System.getProperty("java.version").charAt(2) - '0';
    }

    private Utils() {
    }

    /**
     * 当前日志输出级别不是警告也不是错误
     *
     * @return 是否为DEBUG
     */
    public static boolean isDebugMode() {
        return !logger.isErrorEnabled() && logger.isWarnEnabled();
    }

    /**
     * 计算字符串的长度
     *
     * @param str 字符串
     * @return 字符串的长度
     */
    public static int len(String str) {
        return str.replaceAll("[\\u4E00-\\u9FA5]", "xx").length();
    }

    /**
     * 取指定长度字符串的值。
     * <p/>
     * 如果走过指定上限参数，会以省略号代替。
     *
     * @param str      字符串
     * @param byteSize 字节数量上限。
     * @return String
     */

    public String getAllStringSize(String str, int byteSize) {
        int len = 0;
        char c;
        String words = "";

        if (str == null || "null".equals(str)) {
            return "";
        }

        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                // 字母, 数字
                len++;
            } else {
                if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]")) { // 中文
                    len += 2;
                } else { // 符号或控制字符
                    len++;
                }
            }
            words += String.valueOf(c);
            if (len >= byteSize) {//
                words += "..";
                break;
            }
        }
        return words;
    }

    /**
     * 将字符串转换成整形数组
     *
     * @param s     字符串
     * @param regex 分割符
     * @return int[] 如果s非法，将返回null
     */
    public static Integer[] string2IntArray(String s, String regex) {

        if (StringUtils.isNotEmpty(s)) {

            String[] temp = s.replaceAll("\\s+", "").split(regex);

            int l = temp.length;

            Integer[] ia = new Integer[l];

            try {

                for (int i = 0; i < l; i++) {

                    ia[i] = Integer.parseInt(temp[i]);

                }

                return ia;

            } catch (NumberFormatException e) {
                logger.error(e.getMessage(), e);
            }

        }
        return new Integer[]{};
    }

    /**
     * 将数组转换为List.
     *
     * @param array 数组
     * @param <T>   元素类型
     * @return 列表
     */
    public static <T> List<T> array2List(T[] array) {
        List<T> list = new ArrayList<>();
        if (array != null) {
            for (T element : array) {
                list.add(element);
            }
        }
        return list;
    }

    /**
     * 字符串转义。
     * <p/>
     * 将HTML特殊字符转换为其相关转义符。
     * <p/>
     * 如:
     * <p/>
     * &=》&amp;
     *
     * @param string 字符串
     * @return 转义之后的字符串
     */
    public static String htmlToString(String string) {
        string = StringUtils.stripToEmpty(string);
        string = StringUtils.replace(string, "&", "&amp;");
        string = StringUtils.replace(string, "\"", "&quot;");
        string = StringUtils.replace(string, " ", "&nbsp;");
        string = StringUtils.replace(string, "'", "&#39;");
        string = StringUtils.replace(string, "<", "&lt;");
        string = StringUtils.replace(string, ">", "&gt;");
        string = StringUtils.replace(string, "\t", "   &nbsp;  &nbsp;");
        string = StringUtils.replace(string, "\r", "");
        string = StringUtils.replace(string, "   ", " &nbsp; ");

        return string;
    }

    /**
     * 编码安全的HTML标签字符
     *
     * @param string
     * @return
     */
    public static String encodeHtmlString(String string) {
        string = StringUtils.stripToEmpty(string);
        string = StringUtils.replace(string, "<", "\\u003c");
        string = StringUtils.replace(string, ">", "\\u003e");
        string = StringUtils.replace(string, "&", "\\u0026");
        string = StringUtils.replace(string, "=", "\\u003d");
        string = StringUtils.replace(string, "\"", "\\u0027");
        return string;
    }

    /**
     * 解码安全的HTML标签字符
     *
     * @param string
     * @return
     */
    public static String decodeHtmlString(String string) {
        string = StringUtils.stripToEmpty(string);
        string = StringUtils.replace(string, "\\u003c", "<");
        string = StringUtils.replace(string, "\\u003e", ">");
        string = StringUtils.replace(string, "\\u0026", "&");
        string = StringUtils.replace(string, "\\u003d", "=");
        string = StringUtils.replace(string, "\\u0027", "\"");
        return string;
    }

    /**
     * 字符串转义.
     * <p/>
     * 将HTML转义符还原为相关字符。
     * <p/>
     * 如：
     * <p/>
     * &quot; => "等
     *
     * @param string 字符串
     * @return 转义之后字符串
     */
    public static String stringToHtml(String string) {
        string = StringUtils.stripToEmpty(string);
        string = StringUtils.replace(string, "&amp;", "&");
        string = StringUtils.replace(string, "&quot;", "\"");
        string = StringUtils.replace(string, "&nbsp;", " ");
        string = StringUtils.replace(string, "&#39;", "'");
        string = StringUtils.replace(string, "&lt;", "<");
        string = StringUtils.replace(string, "&gt;", ">");
        string = StringUtils.replace(string, "   &nbsp;  &nbsp;", "\t");
        string = StringUtils.replace(string, "&nbsp; ", "    ");

        return string;
    }

    /**
     * 将一个二维点转换为以逗号分隔的字符串。
     *
     * @param x x轴刻度
     * @param y y轴刻度
     * @return 以逗号分隔的字符串。
     */
    public static String pointToString(int x, int y) {
        return x + "," + y;
    }

    /**
     * 获取当前时间的秒表示。
     *
     * @return 当前时间(秒)
     */
    public static int getCurrentTimeSeconds() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * 获取互联网IP地址。
     * <p/>
     * <p>
     * 此方法仅能获取IPv4
     * </p>
     *
     * @return InetAddress
     */
    public static InetAddress getInternetAddress() {
        InetAddress address = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null && !ip.isLoopbackAddress() && !ip.isSiteLocalAddress() && ip instanceof Inet4Address) {
                        address = ip;
                        break;
                    }
                }
                if (address != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * 获取本机的网络IP地址
     *
     * @return 网络IP地址
     */
    public static String getInternetIP() {
        InetAddress address = getInternetAddress();
        return address == null ? "" : address.getHostAddress();
    }

    /**
     * 获取局域网IP地址。
     * <p/>
     * refer to RFC 1918 // 10/8 prefix // 172.16/12 prefix // 192.168/16 prefix
     * <p/>
     * <p>
     * 此方法仅能获取IPv4
     * </p>
     *
     * @return InetAddress
     */
    public static InetAddress getLocalAddress() {
        InetAddress address = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ip = addresses.nextElement();
                    if (ip != null && !ip.isLoopbackAddress() && ip.isSiteLocalAddress() && ip instanceof Inet4Address) {
                        address = ip;
                        break;
                    }
                }
                if (address != null) {
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return address;
    }

    /**
     * 获取本机IP地址
     * <p/>
     * <p>
     * 如果本机有Internet地址，则返回该地址。
     * </p>
     * <p>
     * 否则返回本机所有局域网地址。
     * </p>
     *
     * @return 本机IP地址。
     */
    public static InetAddress getAddress() {
        InetAddress address = getInternetAddress();
        return address == null ? getLocalAddress() : address;
    }

    /**
     * 同步执行Job.必须启动JobQueueOB.
     * <p/>
     * 永远在第一个队列执行此任务。
     *
     * @param <K>      返回值类型
     * @param callable Job
     * @return 执行结果
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <K> RunnableFuture<K> runSyn(CallableNetJob<K> callable) throws InterruptedException, ExecutionException {
        return runJob(0, callable);
    }

    /**
     * 同步执行Job.必须启动JobQueueOB.
     * <p/>
     * 永远在第一个队列执行此任务。
     *
     * @param <K>      返回值类型
     * @param index    指定队列下标
     * @param callable Job
     * @return 执行结果
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <K> RunnableFuture<K> runJob(int index, CallableNetJob<K> callable) throws InterruptedException, ExecutionException {
        // TODO: for test
        final TestTime runtime = new TestTime();
        callable.addListener(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS, new EventListener<JobProcessEvent>() {
            @Override
            public void on(JobProcessEvent event) {
                runtime.runningTime = System.currentTimeMillis();
            }
        });
        callable.addListener(JobProcessEvent.EVENT_AFTER_JOB_PROCESS, new EventListener<JobProcessEvent>() {

            @Override
            public void on(JobProcessEvent event) {
                long runTime = System.currentTimeMillis() - runtime.runningTime;
                logger.warn(event.emitter().getClass().getName() + " run time :: " + runTime + " ms");
            }
        });
        return JobQueueOB.getInstance().addJob(index, callable);
    }

    /**
     * 异步执行Job.必须启动JobQueueOB.
     * <p/>
     * 永远在第一个队列执行此任务。
     *
     * @param <K>      返回值类型
     * @param runnable Job
     * @param callback Job执行完成时回调函数
     */
    public static <K> void runAsyn(RunnableNetJob<K> runnable, final JobProcessCallback<K> callback) {
        runJob(0, runnable, callback);
    }

    /**
     * 异步执行Job.必须启动JobQueueOB.
     *
     * @param <K>      返回值类型
     * @param index    队列下标
     * @param runnable Job
     * @param callback Job执行完成时回调函数
     */
    public static <K> void runJob(int index, RunnableNetJob<K> runnable, final JobProcessCallback<K> callback) {
        // TODO: for test
        final TestTime runtime = new TestTime();
        runnable.addListener(JobProcessEvent.EVENT_BEFORE_JOB_PROCESS, new EventListener<JobProcessEvent>() {
            @Override
            public void on(JobProcessEvent event) {
                runtime.runningTime = System.currentTimeMillis();
            }
        });
        runnable.addListener(JobProcessEvent.EVENT_AFTER_JOB_PROCESS, new EventListener<JobProcessEvent>() {

            @Override
            public void on(JobProcessEvent event) {
                long runTime = System.currentTimeMillis() - runtime.runningTime;
                logger.warn(event.emitter().getClass().getName() + " run time :: " + runTime + " ms");
            }
        });
        if (callback != null) {
            runnable.addListener(JobProcessEvent.EVENT_AFTER_JOB_PROCESS, new EventListener<JobProcessEvent>() {
                @SuppressWarnings("unchecked")
                @Override
                public void on(JobProcessEvent event) {
                    if (event.getType().equals(JobProcessEvent.EVENT_AFTER_JOB_PROCESS)) {
                        RunnableNetJob<K> job = (RunnableNetJob<K>) event.emitter();
                        callback.call(job.get());
                    }
                }
            });
        }
        JobQueueOB.getInstance().addJob(index, runnable);
    }

    public static String decodeString(byte[] data) {
        return decodeString(data, 0, data.length);
    }

    public static String decodeString(byte[] data, int offset, int count) {
        if (data[offset] < 32) {
            try {
                return new String(data, offset, count, "UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return new String(data, offset, count);
    }

    public static String readLine(InputStream in) throws IOException {
        ByteBuffer buff = new ByteBuffer(256);
        int b = in.read();
        if (b == 10 || b == 13)
            b = in.read();
        while (b > 0) {
            buff.writeByte(b);
            b = in.read();
            if (b == 13 || b == 10)
                break;
        }
        return buff.toString().trim();
    }

    /**
     * 从字节缓冲区读取一行 注意：如果到缓冲区的末尾都找不到换行符，则会抛出异常！！！
     */
    public static String readLine(ByteBuffer in) {
        ByteBuffer buff = new ByteBuffer(in.available());
        int b = in.readUnsignedByte();
        if (b == 10 || b == 13) {
            if (in.available() == 0)
                return "";
            b = in.readUnsignedByte();
        }
        while (b > 0) {
            buff.writeByte(b);
            b = in.readUnsignedByte();
            if (b == 13 || b == 10)
                break;
        }
        return buff.toString().trim();
    }

    public static int getJavaVersion() {
        return JAVA_VERSION;
    }

    public static String getStackTrace(Throwable e) {
        if (e == null)
            return "no exception";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        e.printStackTrace(writer);
        writer.flush();
        writer.close();
        return out.toString();
    }

    public static int indexOf(byte[] data, byte[] src) {
        int u = data.length - src.length;
        for (int i = 0; i < u; i++) {
            boolean b = true;
            for (int j = 0; j < src.length; j++) {
                b &= data[i + j] == src[j];
                if (!b)
                    break;
            }
            if (b)
                return i;
        }
        return -1;
    }

    public static String urlDecode(String src) {
        byte[] data = src.getBytes();
        ByteBuffer buff = new ByteBuffer();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == '+') {
                buff.writeByte(32);
            } else if (data[i] == '%') {
                String str = new String(data, i + 1, 2);
                int ch = Integer.parseInt(str, 16);
                buff.writeByte(ch);
                i += 2;
            } else {
                buff.writeByte(data[i]);
            }
        }
        return buff.toString();
    }

    public static String strDecode(String src) {
        if (src.indexOf('%') < 0 && src.indexOf('+') < 0)
            return src;
        StringBuilder out = new StringBuilder(src.length());
        char ch;
        int count = src.length();
        for (int i = 0; i < count; i++) {
            ch = src.charAt(i);
            if (ch == '%') {
                int index = src.indexOf('%', i + 1);
                String numStr = src.substring(i + 1, index);
                ch = (char) Integer.parseInt(numStr, 16);
                i = index;
            } else if (ch == '+') {
                ch = 32;
            }
            out.append(ch);
        }
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        System.out.println(URLEncoder.encode("<font></font>", "UTF-8"));
    }

    public static String strEncode(String src) {
        char ch;
        StringBuilder out = new StringBuilder(src.length() * 5);
        int count = src.length();
        for (int i = 0; i < count; i++) {
            ch = src.charAt(i);
            if (ch > 127 || ch == '%' || ch == '+' || ch == '"' || ch == '\'' || ch == '<' || ch == '>') {
                out.append('%');
                out.append(Integer.toHexString(ch));
                out.append('%');
            } else if (ch == 32) {
                out.append('+');
            } else {
                out.append(ch);
            }
        }
        return out.toString();
    }

    public static void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
    }

    public static void intersection(Rectangle r1, Rectangle r2, Rectangle r3) {
        int tx1 = r1.x;
        int ty1 = r1.y;
        int rx1 = r2.x;
        int ry1 = r2.y;
        int tx2 = tx1;
        tx2 += r1.width;
        int ty2 = ty1;
        ty2 += r1.height;
        int rx2 = rx1;
        rx2 += r2.width;
        int ry2 = ry1;
        ry2 += r2.height;
        if (tx1 < rx1)
            tx1 = rx1;
        if (ty1 < ry1)
            ty1 = ry1;
        if (tx2 > rx2)
            tx2 = rx2;
        if (ty2 > ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        tx2 = Math.max(tx2, Integer.MIN_VALUE);
//        if (tx2 < Integer.MIN_VALUE)
//            tx2 = Integer.MIN_VALUE;
        ty2 = Math.max(ty2, Integer.MIN_VALUE);
//        if (ty2 < Integer.MIN_VALUE)
//            ty2 = Integer.MIN_VALUE;
        r3.setBounds(tx1, ty1, tx2, ty2);
    }

    public static Object loadObject(Object refObject, String className) {
        try {
            ClassLoader loader = refObject.getClass().getClassLoader();
            Class<?> objClass = loader.loadClass(className);
            return objClass.newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Object loadObject(String className) {
        try {
            Class<?> objClass = Class.forName(className);
            return objClass.newInstance();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String readUTFFile(String filename) {
        File file = new File(filename);
        return readUTFFile(file);
    }

    @SuppressWarnings("resource")
    public static String readUTFFile(File file) {
        if (!file.exists() || file.isDirectory())
            return null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            StringBuilder fileBuffer = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                // System.out.println("line:" + line);
                fileBuffer.append(line);
                fileBuffer.append("\r\n");
            }

            fileBuffer.deleteCharAt(0);
            // fileBuffer.delete(fileBuffer.length() - 2, fileBuffer.length() -
            // 1);
            // System.out.println(fileBuffer.toString());
            // InputStream in = new FileInputStream(file);
            // byte[] data = new byte[in.available()];
            // in.read(data);
            // in.close();
            // String content = new String();
            // return new String(fileBuffer.toString().getBytes(), "UTF-8");
            return fileBuffer.toString();// .substring(1,
            // fileBuffer.toString().length());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readFile(String filename) {
        File file = new File(filename);
        return readFile(file);
    }

    public static byte[] readFile(File file) {
        if (!file.exists() || file.isDirectory())
            return null;
        try {
            InputStream in = new FileInputStream(file);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            return data;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static void writeFile(String filename, byte[] data) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(data);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTime() {
        return getCurrentTime(System.currentTimeMillis());
    }

    public static String getCurrentTime(long time) {
        date.setTime(time);
        return dateFormat.format(date);
    }

    public static String getTimeString(long time) {
        date.setTime(time);
        return dateFormat.format(date);
    }

    public static <T, S> Map<T, S> asMap(T key, S value) {
        Map<T, S> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static <T, S> Map<T, S> join(Map<T, S>... maps) {
        Map<T, S> map = new HashMap<>();
        if (maps != null) {
            for (Map<T, S> value : maps) {
                map.putAll(value);
            }
        }
        return map;
    }


    public static int compareDay(long timeMillions) {
        nowDate.setTime(System.currentTimeMillis());
        date.setTime(timeMillions);

        String str1 = dayDateFormat.format(date);
        String str2 = dayDateFormat.format(nowDate);

        return str1.compareTo(str2);
    }

    /**
     * 从Map里移除指定Value.
     *
     * @param map   被操作对象
     * @param value 需要移除的值
     */
    public static <K, V> K removeValue(Map<K, V> map, V value) {
        if (map == null || value == null) {
            return null;
        }
        if (!map.containsValue(value)) {
            return null;
        }
        K key = null;
        for (Entry<K, V> entry : map.entrySet()) {
            if (value == entry.getValue() || value.equals(entry.getValue())) {
                key = entry.getKey();
                break;
            }
        }
        if (key != null) {
            map.remove(key);
        }
        return key;
    }

    /**
     * 将对象所有实例变量赋值给另外一个新实例。
     *
     * @param obj 实例
     * @return 新实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(T obj) {
        T o = null;
        try {
            o = (T) obj.getClass().newInstance();
            Field[] all = ReflectionUtils.getDeclaredAndInheritedFields(obj.getClass(), false);
            for (Field field : all) {
                field.setAccessible(true);
                field.set(o, field.get(obj));
                field.setAccessible(false);
            }
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException e) {
//            e.printStackTrace();
            logger.error("copy object error,", e);
        }
        return o;
    }

    /**
     * 将src实例的所有实例变量赋值给to的相应实例变量
     *
     * @param from 源实例
     * @param to   目标实例
     */
    public static <T> void copy(T from, T to) {
        try {
            Field[] all = ReflectionUtils.getDeclaredAndInheritedFields(from.getClass(), false);
            for (Field field : all) {
                field.setAccessible(true);
                field.set(to, field.get(from));
                field.setAccessible(false);
            }
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}