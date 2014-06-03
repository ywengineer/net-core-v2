/**
 *
 */
package com.handee.utils;

import com.handee.helper.Reference;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wang
 */
public class MathUtils {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MathUtils.class);

    /**
     * 格式化字符串。
     * <p/>
     * 此方法仅用于格式化带有此种占位符{数字，从0开始}的字符串。
     *
     * @param src    源字符串
     * @param params 参数
     * @return 格式化结果
     */
    public static String formatString(String src, Object[] params) {
        if (StringUtils.isEmpty(src) || params == null || params.length == 0) {
            return src;
        }
        for (int i = 0; i < params.length; i++) {
            src = src.replaceAll("\\{" + i + "\\}", getString(params[i]));
        }
        return src;
    }

    /**
     * 获取参数之间的随机数
     *
     * @param from 起始值
     * @param to   结束值
     * @return 随机值
     */
    public static int random(int from, int to) {
        from = Math.min(from, to);
        to = Math.max(from, to);
        return from + RandomUtils.nextInt(to - from + 1);
    }

    /**
     * 返回指定位数的随机字符串
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        if (length == 0) {
            length = 6;
        }
        String string = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        return RandomStringUtils.random(length, string);
    }

    /**
     * 返回指定位数的随机数
     *
     * @param length 随机位数长度
     * @return id 随机数
     */
    public static int getRandomNumber(int length) {
        if (length == 0) {
            length = 6;
        }
        int result = (int) (Math.random() * Math.pow(10, length));
        while (result < Math.pow(10, length - 1)) {
            result = (int) (Math.random() * Math.pow(10, length));
        }
        return result;
    }

    /**
     * 如果发生异常返回１
     *
     * @param object 整数对象
     * @return 如果发生异常返回１
     */
    public static int getIntOverZero(Object object) {
        try {
            return Integer.parseInt(object.toString().trim());
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * 如果发生异常返回１
     *
     * @param object 对象表示的长整数
     * @return 如果发生异常返回１
     */
    public static long getLongOverZero(Object object) {
        try {
            return Long.parseLong(object.toString().trim());
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * 如果发生异常返回0
     *
     * @param object 对象表示的整数
     * @return 整数
     */
    public static int getInteger(Object object) {
        try {
            return new BigDecimal(getString(object)).intValue();
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String getString(Object obj) {
        return obj == null ? "" : String.valueOf(obj);
    }

    public static boolean getBoolean(Object obj) {
        return Boolean.parseBoolean(getString(obj));
    }

    public static byte getByte(Object obj) {
        return (byte) getInteger(obj);
    }

    public static char getChar(Object obj) {
        return getString(obj).toCharArray()[0];
    }

    public static double getDouble(Object obj) {
        try {
            return Double.parseDouble(getString(obj));
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            LOGGER.error("getDouble Error param[" + obj + "] :: ", e);
        }
        return 0d;
    }

    public static float getFloat(Object obj) {
        try {
            return Float.parseFloat(getString(obj));
        } catch (NumberFormatException e) {
            // e.printStackTrace();
            LOGGER.error("getFloat Error param[" + obj + "] :: ", e);
        }
        return 0.0f;
    }

    public static short getShort(Object obj) {
        return (short) getInteger(obj);
    }

    /**
     * 如果发生异常返回0
     *
     * @param object 对象表示Long
     * @return long
     */
    public static long getLong(Object object) {
        try {
            return Long.parseLong(object.toString().trim());
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 检测+10%,-10% +10 -10是否大于指定的数,或百分比
     *
     * @param conditionValue 条件表达式
     * @param propertyValue  属性值
     */
    public static int update(String conditionValue, int propertyValue) {

        return MathUtils.update(conditionValue, propertyValue, 0, 0);
    }

    /**
     * 检测+10%,-10% +10 -10是否大于指定的数,或百分比
     *
     * @param conditionValue 条件表达式
     * @param propertyValue  属性值
     * @param propertyValue2 属性百分比时分母
     * @param offer          倍数偏移量
     * @return int
     */
    public static int update(String conditionValue, int propertyValue, int propertyValue2, int offer) {

        if (conditionValue.indexOf('+') > -1) {// 大于
            conditionValue = conditionValue.replace('+', ' ');

            if (conditionValue.indexOf('%') > -1) {// 百分比

                int value = MathUtils.getInteger(conditionValue.replace('%', ' '));
                try {
                    if (offer != 0) {
                        return propertyValue + offer * propertyValue * value / propertyValue2;
                    } else {
                        return propertyValue + propertyValue * value / propertyValue2;
                    }

                } catch (Exception ex) {
                    LOGGER.error("更新条件表达式时没有提供完整参数 表达式=%+" + value);
                    return value;
                }

            } else {// 固定数值
                int value = MathUtils.getInteger(conditionValue);
                if (offer != 0) {
                    return propertyValue + offer * value;
                } else {
                    return propertyValue + value;
                }
            }
        } else {// 小于
            if (conditionValue.indexOf('%') > -1) {// 百分比
                int value = Math.abs(MathUtils.getInteger(conditionValue.replace('%', ' ')));
                try {
                    return propertyValue - propertyValue * value / propertyValue2;
                } catch (Exception ex) {
                    LOGGER.error("更新条件表达式时没有提供完整参数 表达式=%-" + value);
                    return value;
                }
            } else {// 固定数
                int value = Math.abs(MathUtils.getInteger(conditionValue));

                return propertyValue - value;
            }
        }
    }

    /**
     * 检测+10%,-10% +10 -10是否大于指定的数,或百分比
     *
     * @param conditionValue 条件表达式
     * @param propertyValue  属性值
     * @param propertyValue2 属性百分比时分母
     * @return int
     */
    public static boolean check(String conditionValue, int propertyValue, int propertyValue2) {

        if (conditionValue.indexOf('+') > -1) {// 大于
            conditionValue = conditionValue.replace('+', ' ');

            if (conditionValue.indexOf('%') > -1) {// 百分比

                int value = MathUtils.getInteger(conditionValue.replace('%', ' '));
                try {
                    if (propertyValue * 100 / (propertyValue2) > value) {
                        return true;
                    }
                } catch (Exception ex) {
                    LOGGER.error("更新条件表达式时没有提供完整参数 表达式=%+" + value);
                    return false;
                }

            } else {// 固定数值
                int value = MathUtils.getInteger(conditionValue);
                if (propertyValue > value) {// 属性值是否满足大于中指定的值是则触发
                    return true;
                }
            }
        } else if (conditionValue.indexOf('-') > -1) {// 小于
            if (conditionValue.indexOf('%') > -1) {// 百分比
                int value = Math.abs(MathUtils.getInteger(conditionValue.replace('%', ' ')));
                try {
                    if (propertyValue * 100 / (propertyValue2) < value) {
                        return true;
                    }
                } catch (Exception ex) {
                    LOGGER.error("更新条件表达式时没有提供完整参数 表达式=%-" + value);
                    return false;
                }
            } else {// 固定数
                int value = Math.abs(MathUtils.getInteger(conditionValue.replace('%', ' ')));
                if (propertyValue < value) {// 属性值是否满足小于指定的值是则触发
                    return true;
                }
            }
        } else {
            // 小于
            if (conditionValue.indexOf('%') > -1) {// 百分比
                int value = Math.abs(MathUtils.getInteger(conditionValue.replace('%', ' ')));
                try {
                    if (propertyValue * 100 / (propertyValue2) == value) {
                        return true;
                    }
                } catch (Exception ex) {
                    LOGGER.error("更新条件表达式时没有提供完整参数 表达式=%=" + value);
                    return false;
                }
            } else {// 固定数
                int value = Math.abs(MathUtils.getInteger(conditionValue.replace('%', ' ')));
                if (propertyValue == value) {// 属性值是否满足等于指定的值是则触发
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 将String转换成Map<Integer, Integer>。
     * <p/>
     * 每项之间的分隔符为,。
     * <p/>
     * 每对值之间的分隔符为:.
     * <p/>
     * 如：id:value,id:value,id:value
     *
     * @param content String
     * @return Map
     */
    public static Map<Integer, Integer> stringToMapWithIntegerKey(String content) {
        Map<Integer, Integer> reVal = new HashMap<>();
        if (StringUtils.isNotEmpty(content)) {
            String[] stone = content.split(",");
            for (String pairs : stone) {
                String[] kv = pairs.split(":");
                if (kv.length != 2) {
                    continue;
                }
                reVal.put(getInteger(kv[0]), getInteger(kv[1]));
            }
        }
        return reVal;
    }

    /**
     * 将String转换成Map<String, Integer>。
     * <p/>
     * 每项之间的分隔符为,。
     * <p/>
     * 每对值之间的分隔符为:。
     * <p/>
     * 如：key:value,key:value,key:value
     *
     * @param content String
     * @return Map
     */
    public static Map<String, Integer> stringToMapWithStringKey(String content) {
        Map<String, Integer> reVal = new HashMap<>();
        if (StringUtils.isNotEmpty(content)) {
            String[] stone = content.split(",");
            for (String pairs : stone) {
                String[] kv = pairs.split(":");
                if (kv.length != 2) {
                    continue;
                }
                reVal.put(kv[0], getInteger(kv[1]));
            }
        }
        return reVal;
    }

    /**
     * 将String转换成Map<Enum, Integer>。
     * <p/>
     * 每项之间的分隔符为,。
     * <p/>
     * 每对值之间的分隔符为:。
     * <p/>
     * 如：key:value,key:value,key:value
     *
     * @param content String
     * @param keyCls  key的类文件描述
     * @param <T>     枚举类型
     * @return Map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends Enum> Map<T, Integer> stringToMapWithEnumKey(Class<T> keyCls, String content) {
        Map<T, Integer> reVal = new HashMap<>();
        if (StringUtils.isNotEmpty(content)) {
            String[] stone = content.split(",");
            for (String pairs : stone) {
                String[] kv = pairs.split(":");
                if (kv.length != 2) {
                    continue;
                }
                reVal.put((T) Enum.valueOf(keyCls, kv[0]), getInteger(kv[1]));
            }
        }
        return reVal;
    }

    /**
     * 将String转换成List<Integer>。
     * <p/>
     * 如：id,id,id
     *
     * @param content   String
     * @param separator 分隔符
     * @return List<Integer>
     */
    public static List<Integer> stringToIntegerList(String content, String separator) {
        List<Integer> reval = new ArrayList<>();
        if (StringUtils.isNotEmpty(content)) {
            String vals[] = content.split(separator);
            for (String val : vals) {
                reval.add(getInteger(val));
            }
        }
        return reval;
    }

    /**
     * 按机率随机。
     * <p/>
     * 配置格式=id:weight,id:weight。
     * 如果不包括任何分隔符，直接返回文本的整数表示
     *
     * @param str 机率配置
     * @return 配置ID
     */
    public static int random(String str) {
        //
        if (!StringUtils.contains(str, ',') && !StringUtils.contains(str, ':')) {
            return getInteger(str);
        }
        // 所有配置ID和权重
        String[] idsInfo = str.split(",");
        // 总权重值
        int allWeight = 0;
        List<Reference<Integer, Integer>> config = new ArrayList<>();

        for (String idInfo : idsInfo) {
            String[] info = idInfo.split(":");
            int id = getInteger(info[0]);
            int weight = getInteger(info[1]);
            // 权重
            config.add(new Reference<>(id, weight));
            allWeight += weight;
        }
        int randomAuth = RandomUtils.nextInt(allWeight);

        for (Reference<Integer, Integer> reference : config) {
            // 当前权重
            if (randomAuth <= reference.value) {
                return reference.key;
            } else {
                randomAuth -= reference.value;
            }
        }
        return 0;
    }

    /**
     * 将String转换成数组
     * <p/>
     * 如：id,id,id
     *
     * @param content   String
     * @param separator 分隔符
     * @return []
     */
    public static int[] stringToIntArray(String content, String separator) {
        if (StringUtils.isNotEmpty(content)) {
            String vals[] = content.split(separator);
            int[] reval = new int[vals.length];
            for (int i = 0; i < reval.length; i++) {
                reval[i] = getInteger(vals[i]);
            }
            return reval;
        }
        return null;
    }
}
