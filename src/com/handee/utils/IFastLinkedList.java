/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * IFastLinkedList.java
 * 
 * 2013-11-14 上午9:26:48
 * 
 *******************************************************************/
package com.handee.utils;

import com.handee.utils.FastLinkedList.Node;

/**
 * 
 * Class Description
 * 
 * @author Mark
 * 
 */
public interface IFastLinkedList<E> {
	/**
	 * 获取指定位置的节点。
	 * 
	 * @param index
	 * @return
	 */
	Node<E> getNode(int index);

	/**
	 * 获取头节点。
	 * 
	 * @return
	 */
	Node<E> first();

	/**
	 * 获取尾节点。
	 * 
	 * @return
	 */
	Node<E> last();

	/**
	 * 从开始节点向后获取指定偏移量节点.
	 * 
	 * 即偏移量包括开始节点。
	 * 
	 * @param begin
	 *            开始节点
	 * @param offset
	 *            偏移量.
	 * @return
	 */
	Node<E> get(Node<E> begin, int offset);

	/**
	 * 获取指定节点的下一节点。
	 * 
	 * 如果已是尾巴节点，返回空。
	 * 
	 * @param node
	 *            节点
	 * @return
	 */
	Node<E> next(Node<E> node);

	/**
	 * 替换将节点元素
	 * 
	 * @param node
	 *            节点
	 * @param element
	 *            新元素
	 * @return
	 */
	E set(Node<E> node, E element);

	/**
	 * 添加元素到列表末尾
	 * 
	 * @param e
	 *            元素。
	 * @return
	 */
	boolean add(E e);

	/**
	 * 获取列表元素数量。
	 * 
	 * @return
	 */
	int size();

	/**
	 * 清空列表。
	 */
	void clear();

	/**
	 * 将新元素添加到指定节点前。
	 * 
	 * @param node
	 *            节点
	 * @param element
	 *            新元素
	 */
	void add(Node<E> node, E element);

	/**
	 * 移除指定节点。
	 * 
	 * 如果节点为无效节点（没前置节点也没后置节点），不对列表做任何改动。
	 * 
	 * @param node
	 *            节点
	 * @return
	 */
	E remove(Node<E> node);

	/**
	 * 删除指定位置节点。
	 * 
	 * @param index
	 * @return
	 */
	E remove(int index);
}
