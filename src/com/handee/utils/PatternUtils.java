/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * PatternUtils.java
 *
 * 2013-9-17 上午11:13:27
 *
 *******************************************************************/
package com.handee.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 正则表达式检测工具类。
 * <p/>
 * 用于检测指定字符串是否符合相关模式，如：IP、EMAIL etc.
 *
 * @author Mark
 */
public class PatternUtils {
    public static interface PredicatePattern<T> {
        public boolean evaluate(T object);
    }

    /**
     * 检测指定字符串是否为手机号码。
     *
     * @param mobiles 手机号码。
     * @return 是否为手机号码.
     */
    public static boolean isMobileNO(String mobiles) {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 检测指定字符串是否为邮箱。
     *
     * @param email 邮箱地址.
     * @return 是否为正常的邮箱地址.
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 检测指定字符串是否为正确的IP地址。
     *
     * @param ip IP地址.
     * @return 是否是正常的IP地址。
     */
    public static boolean isIp(String ip) {
        String str = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(str);
        Matcher m = p.matcher(ip);
        return m.matches();
    }

    /**
     * 从集合中查找
     *
     * @param collections 集合
     * @param predicate   查找条件
     * @param <T>         集体元素类型
     * @return 查找结果
     */
    public static <T> T find(PredicatePattern<T> predicate, Collection<T>... collections) {
        if (collections != null && predicate != null) {
            for (Collection<T> collection : collections) {
                if (collection != null) {
                    for (Iterator<T> iter = collection.iterator(); iter.hasNext(); ) {
                        T item = iter.next();
                        if (predicate.evaluate(item)) {
                            return item;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 查找满足条件的集合元素
     *
     * @param collections 集合
     * @param predicate   查找条件
     * @param <T>         集体元素类型
     * @return 查找结果
     */
    public static <T> List<T> findMore(PredicatePattern<T> predicate, Collection<T>... collections) {
        List<T> all = new ArrayList<>();
        if (collections != null && predicate != null) {
            for (Collection<T> collection : collections) {
                if (collection != null) {
                    for (Iterator<T> iter = collection.iterator(); iter.hasNext(); ) {
                        T item = iter.next();
                        if (predicate.evaluate(item)) {
                            all.add(item);
                        }
                    }
                }
            }
        }
        return all;
    }
}
