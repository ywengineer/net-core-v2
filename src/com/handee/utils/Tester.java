/*******************************************************************
 *
 * Copyright (C) 2013 - 2014 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * Tester.java
 *
 * 14-3-11 上午10:35
 *
 *******************************************************************/
package com.handee.utils;

import net.sf.ehcache.util.ProductInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * com.handee.utils.Tester.java Created by Author.
 * <p/>
 * Author: Mark
 * <p/>
 * Email: ywengineer@gmail.com
 * <p/>
 * Date: 14-3-11 上午10:35
 */
public class Tester {

    public static void main(String args[]) {
        String resource = "/net/sf/ehcache/version.properties";
        InputStream in = ProductInfo.class.getResourceAsStream(resource);
        final Properties props = new Properties();

        if (in == null) {
            throw new RuntimeException("Can't find resource: " + resource);
        }

        try {
            props.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
            } catch (IOException e2) {
                // ignore
                e2.printStackTrace();
            }
        }
    }
}
