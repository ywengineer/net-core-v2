package com.handee.net.message;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Message 的摘要说明。
 * 
 * @author Mark
 */
public abstract class NetMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5489078160352713381L;
	private Object source;
	private Object content;
	private long sessionId;
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public NetMessage(Object content) {
		setContent(content);
	}

	public NetMessage(Object content, Object source) {
		setContent(content);
		setSource(source);
	}

	public void setAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public void setAttributes(Map<String, Object> attri) {
		attributes.putAll(attri);
	}

	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(this.attributes);
	}

	public Object getSource() {
		return source;
	}

	public Object getContent() {
		return content;
	}

	public void setSource(Object obj) {
		source = obj;
	}

	public void setContent(Object obj) {
		content = obj;
	}

	/**
	 * @return the sessionId of connection
	 */
	public long getSessionId() {
		return sessionId;
	}

	/**
	 * @param sessionId
	 *            the sessionId to set
	 */
	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}
}