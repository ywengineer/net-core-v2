/**
 * 
 */
package com.handee.utils;

/**
 * @author wangyongdong
 * 随机样本
 */
public class Sample {
	
	/**
	 * 样本标识
	 */
	private String id;
	
	/**
	 * 样本对象
	 */
	private Object object;
	
	/**
	 * 样本触发几率
	 */
	private int rate;
	
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
}
