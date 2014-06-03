/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * com.handee.interfaces.IBufferable.java
 * 
 * 2013-6-24 上午11:22:54
 * 
 *******************************************************************/
package com.handee.interfaces;

import com.handee.utils.ByteBuffer;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public interface IBufferable {

	/**
	 * 将数据写到缓冲区
	 * 
	 * @param buffer
	 *            缓冲区
	 */
	public void writeTo(ByteBuffer buffer);

	/**
	 * 从缓冲区中读取数据。
	 * 
	 * @param buffer
	 *            缓冲区
	 */
	public void readFrom(ByteBuffer buffer);
}
