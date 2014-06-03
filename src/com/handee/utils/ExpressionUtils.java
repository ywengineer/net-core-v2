/*******************************************************************
 *
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 *
 * 成都撼地科技有限责任公司 版权所有
 *
 * ExpressionExcutor.java
 *
 * 2013 2013-5-21 下午4:25:37
 *
 *******************************************************************/
package com.handee.utils;

import bsh.EvalError;
import bsh.Interpreter;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 表达式工具类。
 *
 * @author Mark
 */
public class ExpressionUtils {
    private static final Logger logger = Logger.getLogger(ExpressionUtils.class);

    /**
     * 执行一个简单的表达式。
     * <p/>
     * 支持原生的Java和JavaScript.
     * <p/>
     * <p>
     * Example:
     * </p>
     * <p/>
     * <p>
     * 1: value != null && value instanceof <code>java.lang.Runnable</code>
     * </p>
     * <p/>
     * <p>
     * 2: value >= 2
     * </p>
     *
     * @param expression 表达式
     * @param params     参数对
     * @return 表达式结果
     */
    public static Object eval(String expression, Map<String, Object> params) {
        try {
            Interpreter inter = new Interpreter();
            if (params != null) {
                for (String key : params.keySet()) {
                    inter.set(key, params.get(key));
                }
            }
            return inter.eval(expression);
        } catch (EvalError e) {
            logger.error("expression eval error :: ", e);
        }
        return null;
    }


    /**
     * <b> <i> 同方法eval(String expression, Map<String, Object> params) </i> <b>
     * <p/>
     * <p>
     * 此方法仅用于转换返回值类型为java.lang.Integer
     * </p>
     *
     * @param expression 表达式
     * @param params     参数对
     * @return 表达式结果
     */
    public static int evalWithInteger(String expression, Map<String, Object> params) {
        return MathUtils.getInteger(eval(expression, params));
    }

    /**
     * <b> <i> 同方法eval(String expression, Map<String, Object> params) </i> <b>
     * <p/>
     * <p>
     * 此方法仅用于转换返回值类型为java.lang.Integer
     * </p>
     *
     * @param expression 表达式
     * @return 表达式结果
     */
    public static int evalWithInteger(String expression) {
        return MathUtils.getInteger(eval(expression, null));
    }

    /**
     * <b> <i> 同方法eval(String expression, Map<String, Object> params) </i> <b>
     * <p/>
     * <p>
     * 此方法仅用于转换返回值类型为java.lang.Boolean
     * </p>
     *
     * @param expression 表达式
     * @return 表达式结果
     */
    public static boolean evalWithBoolean(String expression) {
        return MathUtils.getBoolean(eval(expression, null));
    }

    /**
     * <b> <i> 同方法eval(String expression, Map<String, Object> params) </i> <b>
     * <p/>
     * <p>
     * 此方法仅用于转换返回值类型为java.lang.String
     * </p>
     *
     * @param expression 表达式
     * @param params     参数对
     * @return 表达式结果
     */
    public static String evalWithString(String expression, Map<String, Object> params) {
        return MathUtils.getString(eval(expression, params));
    }
}
