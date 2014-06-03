/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ClassReference.java
 *
 * 2013 2013-5-30 下午2:50:08
 *
 *******************************************************************/
package com.handee.helper;

import java.io.Serializable;

/**
 * 引用定义，与{@link java.util.Map.Entry}一样。
 *
 * @author Mark
 */
public class Reference<K, V> implements Serializable {
    public K key;
    public V value;

    public Reference(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void free() {
        this.key = null;
        this.value = null;
    }
}
