/*******************************************************************
 *
 * Copyright © 2008 - 2009  All Rights Reserved. 
 *
 * @Version : 1.0
 *
 * @Filename SimpleRandom.java
 *
 * @author wangyongdong
 *
 * @Eamil wangyongdong@webgame.com.cn
 *
 * @Date 2009-9-22 下午12:17:11
 *
 *******************************************************************/

package com.handee.utils;

import java.util.*;

/**
 * @author wangyongdong
 */
public class SimpleRandom {

    /**
     * 标准样本空间为10000
     */
    public static int RANDOM_MAX_INT = 10000;

    /**
     * 命中样本为0
     */
    public static int RANDOM_TARGET_INT = 0;

    /**
     * 指定随机数分子 60%
     *
     * @param range 随机数分子
     * @return 是否超过指定分子
     */

    public static boolean factor(int range) {
        return range > RandomUtils.nextInt(SimpleRandom.RANDOM_MAX_INT);
    }

    /**
     * 指定随机数分母 1/4
     *
     * @param rate 随机数分母
     * @return 随机数分母是否为0
     */
    public static boolean denominator(int rate) {
        return rate <= SimpleRandom.RANDOM_MAX_INT || RandomUtils.nextInt(rate) == SimpleRandom.RANDOM_TARGET_INT;
    }

    /**
     * 根据指定的list 随机返回list中的某个对象
     *
     * @param list 列表
     * @return 从列表中随机选取
     */
    public static Object random(List<Object> list) {
        int rand = RandomUtils.nextInt(list.size());

        return list.get(rand);
    }

    /**
     * 根据指定的queue 随机返回queue中的某个对象
     *
     * @param queue 队列
     * @return 随机元素
     */
    public static Object random(Queue<Object> queue) {

        int rand = RandomUtils.nextInt(queue.size());

        int i = 0;
        Object ret = new Object();
        for (Object object : queue) {
            if (i == rand) {
                ret = object;
                break;
            }
            i++;
        }

        return ret;
    }

    /**
     * 根据指定的list 随机返加list中的指定数量的对象
     *
     * @param <T>  列表中元素类型
     * @param list 列表
     * @param size 最大范围
     * @return 随机元素
     */
    public static <T> List<T> random(List<T> list, int size) {

        Set<Integer> set = SimpleRandom.random(list.size(), size);

        List<T> ret = new ArrayList<>();

        for (Integer rand : set) {
            ret.add(list.get(rand));
        }

        return ret;
    }

    /**
     * 根据指定范围和指定的位数 返回不重覆的指定数量的随机数列
     *
     * @param rate 范围
     * @param size 随机数范围
     * @return 随机集合
     */
    public static Set<Integer> random(int rate, int size) {

        if (size > rate)
            size = rate;

        Set<Integer> set = new HashSet<>();

        while (size > 0) {
            int len = set.size();
            int rand = RandomUtils.nextInt(rate);
            set.add(rand);
            if (set.size() > len) {
                size--;
            }
        }

        return set;
    }

    /**
     * 根据样本对象的触发几率,返回对象
     *
     * @param set 样本集合
     * @return 样本，概率触发
     */
    public static Sample random(Set<Sample> set) {
        Sample ret = null;

        for (Sample sample : set) {
            int rate = sample.getRate();
            if (SimpleRandom.factor(rate)) // 如果触发就返回该对象
            {
                ret = sample;
                break;
            }
        }

        return ret;
    }

}
