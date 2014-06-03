/*******************************************************************
 * 
 * Copyright (C) 2013 ( Handee )Information Technology Co., Ltd.
 * 
 * 成都撼地科技有限责任公司 版权所有
 * 
 * HandeeChannel.java
 * 
 * 2013 2013-5-27 上午10:45:47
 * 
 *******************************************************************/
package com.handee.cluster;

import com.handee.utils.SystemUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.log4j.Logger;
import org.jgroups.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * 
 * class description
 * 
 * @author Mark
 * 
 */
public class MessageChannel extends JChannel implements ChannelListener, Receiver {
	private static final Logger logger = Logger.getLogger(MessageChannel.class);
	private IClusterMessageHandler handler;
	// 所有集群成员
	private List<Address> members = new LinkedList<>();
	// 除自己之外的所有成员
	private List<Address> membersExcludeSelf = new LinkedList<>();
	// 同类型的成员
	private List<Address> membersOfSameCategory = new LinkedList<>();
	// 与该成员类型不相同的所有成员
	private List<Address> membersOfOtherCategory = new LinkedList<>();
	// 成员分类列表
	private Map<String, List<Address>> categories = new HashMap<>();

	public MessageChannel() throws Exception {
		this("UDP");
	}

	/**
	 * @throws Exception
	 */
	public MessageChannel(String protocal) throws Exception {
		super("UDP".equalsIgnoreCase(protocal) ? "handee-cluster-udp.xml" : "handee-cluster-tcp.xml");
		setReceiver(this);
		addChannelListener(this);
		setDiscardOwnMessages(true);
	}

	/**
	 * 集群消息处理器
	 * 
	 * @param handler
	 *            处理器
	 */
	public void setHandler(IClusterMessageHandler handler) {
		this.handler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MessageListener#receive(org.jgroups.Message)
	 */
	@Override
	public void receive(Message msg) {
		logger.info("receive cluster message [source=" + msg.getSrc() + "] :: " + msg);
		if (handler != null) {
			handler.onMessage(msg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MessageListener#getState(java.io.OutputStream)
	 */
	@Override
	public void getState(OutputStream output) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MessageListener#setState(java.io.InputStream)
	 */
	@Override
	public void setState(InputStream input) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MembershipListener#viewAccepted(org.jgroups.View)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void viewAccepted(View joinedView) {
		List<Address> newMember;
		SystemUtils.printSection("cluster member infomation");
		if (members.size() <= 0) {
			newMember = joinedView.getMembers();
			System.out.println("new member join in this cluster :: " + newMember);
		} else {
			if (joinedView.getMembers().size() > members.size()) {
				// add
				newMember = ListUtils.subtract(joinedView.getMembers(), members);
				System.out.println("new member join in this cluster :: " + newMember);
			} else {
				// leave
				newMember = ListUtils.subtract(members, joinedView.getMembers());
				System.out.println("member leave this cluster :: " + newMember);
			}
		}
		members.clear();
		membersExcludeSelf.clear();
		membersOfSameCategory.clear();
		membersOfOtherCategory.clear();
		clearCategories();

		members.addAll(joinedView.getMembers());

		membersExcludeSelf.addAll(members);
		membersExcludeSelf.remove(getAddress());

		for (Address address : members) {
			// logical name
			String logicalName = getName(address);
			// not same logical name.
			if (!getName().equals(logicalName)) {
				membersOfOtherCategory.add(address);
			} else {// same category
				membersOfSameCategory.add(address);
			}
			//
			List<Address> membersWithSameLogicalName = categories.get(logicalName);
			// not exist
			if (membersWithSameLogicalName == null) {
				// new
				membersWithSameLogicalName = new LinkedList<>();
				// add
				categories.put(logicalName, membersWithSameLogicalName);
			}
			// add to category list.
			membersWithSameLogicalName.add(address);
		}

		System.out.println("all of members in this cluster :: " + members);
		System.out.println("members in this cluster exclude self :: " + membersExcludeSelf);
		System.out.println("members of other category ::　" + membersOfOtherCategory);
		System.out.println("members of same category ::　" + membersOfSameCategory);
		System.out.println("members group by category :: " + categories);
		SystemUtils.printSection("【 END 】");
	}

	protected void clearCategories() {
		for (String key : categories.keySet()) {
			categories.get(key).clear();
		}
		categories.clear();
	}

	public List<Address> getMembers() {
		return Collections.unmodifiableList(members);
	}

	public List<Address> getMembersExcludeSelf() {
		return Collections.unmodifiableList(membersExcludeSelf);
	}

	public List<Address> getMembersOfSameLogicalAddress() {
		return Collections.unmodifiableList(membersOfSameCategory);
	}

	public List<Address> getMembersOfOtherLogicalAddress() {
		return Collections.unmodifiableList(membersOfOtherCategory);
	}

	public List<Address> getMembersByLogicalName(String logicalName) {
		List<Address> members = categories.get(logicalName);
		members = members == null ? new ArrayList<Address>() : members;
		return Collections.unmodifiableList(members);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MembershipListener#suspect(org.jgroups.Address)
	 */
	@Override
	public void suspect(Address suspected_mbr) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MembershipListener#block()
	 */
	@Override
	public void block() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.MembershipListener#unblock()
	 */
	@Override
	public void unblock() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.ChannelListener#channelConnected(org.jgroups.Channel)
	 */
	@Override
	public void channelConnected(Channel channel) {
		logger.info("Channel connected :: " + channel.getAddress());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.ChannelListener#channelDisconnected(org.jgroups.Channel)
	 */
	@Override
	public void channelDisconnected(Channel channel) {
		logger.info("Channel disconnected :: " + channel.getAddress());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jgroups.ChannelListener#channelClosed(org.jgroups.Channel)
	 */
	@Override
	public void channelClosed(Channel channel) {
		logger.info("Channel closed :: " + channel.getAddress());
	}

}
