/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * ContextEventListener.java
 * 
 * 2013 2013-6-13 上午11:22:56
 * 
 *******************************************************************/
package com.handee;

/**
 * 
 * 上下文启动器事件监听器
 * 
 * @author Mark
 * 
 */
public interface ContextEventListener {
	/**
	 * 上下文件启动成功。
	 */
	void started();

	/**
	 * 上下文件已关闭。
	 */
	void stoped();
}
