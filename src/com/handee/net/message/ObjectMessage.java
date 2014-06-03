/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * BufferMessage.java
 * 
 * 2013 2013-5-23 下午1:15:39
 * 
 *******************************************************************/
package com.handee.net.message;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class ObjectMessage extends NetMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6412813093981553543L;

	/**
	 * 
	 * @param content
	 *            消息内容
	 */
	public ObjectMessage(byte[] content) {
		this(content, null);
	}

	/**
	 * @param content
	 *            消息内容
	 * @param source
	 *            消息来源
	 */
	public ObjectMessage(byte[] content, Object source) {
		super(content, source);
	}

	public byte[] getBytes() {
		return (byte[]) getContent();
	}
}
