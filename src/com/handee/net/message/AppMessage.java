package com.handee.net.message;

import com.handee.utils.ByteBuffer;

import java.io.Serializable;

/**
 * AppMessage 应用程序消息
 * 
 * 
 * @author Mark
 * 
 */
public class AppMessage extends NetMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int MESSAGE_TYPE_NULL = 0;

	private int type;

	public AppMessage(int type) {
		this(type, 256);
	}

	public AppMessage(int type, int size) {
		this(type, new ByteBuffer(size), null);
	}

	public AppMessage(int type, ByteBuffer buffer) {
		this(type, buffer, null);
	}

	public AppMessage(int type, ByteBuffer buffer, Object source) {
		super(buffer, source);
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public ByteBuffer getBuffer() {
		return (ByteBuffer) getContent();
	}

	public String toString() {
		return getClass().getName() + ":" + type + ";" + getBuffer().capacity();
	}

	public static AppMessage create(int type) {
		return new AppMessage(type);
	}
}