/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * IntRange.java
 *
 * 14-5-9 下午3:25
 *
 *******************************************************************/
package com.handee.utils;

import java.io.Serializable;

/**
 * com.handee.utils.IntRange.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-5-9 下午3:25
 */
public class IntRange implements Serializable {
    private int min;
    private int max;

    /**
     * 构造区间
     *
     * @param min 区间最小值
     * @param max 区间最大值
     */
    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * 构造区间
     * <p/>
     * 最大值为整数的最大值
     *
     * @param min 区间最小值
     */
    public IntRange(int min) {
        this(min, Integer.MAX_VALUE);
    }

    /**
     * 查看指定值是否在此区间
     *
     * @param number 数值
     * @return 是否在此区间
     */
    public boolean between(int number) {
        return number >= min && number <= max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
