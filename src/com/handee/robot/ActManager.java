package com.handee.robot;

import com.handee.net.message.AppMessage;

/**
 * 类说明：模拟操作
 * 
 * @author
 * */

public class ActManager {

	/** 动作类型(开始地图,切换地图,邀请组队,确认组队,打开商城,使用道具,战斗) */
	public static final int MAP_START = 0, MAP_TO = 1, TEAM_APPLY = 2, TEAM_CONFIRM = 3, OPEN_SHOP = 4, USE_ITEM = 5, ATTACK = 6;
	/** 商城动作类型(购买,出售) */
	public static final int BUY_ITEM = 0, SELL_ITEM = 1;
	/** 消息处理管理 */
	private Robot owner;
	/** 通信消息 */
	private RobotData data;
	/** 当前场景 */
	private int currentMap;

	/** 构造 */
	public ActManager() {

	}

	/** 构造 */
	public ActManager(RobotData data) {
		this.data = data;
	}

	/** 获得数据 */
	public Robot getOwner() {
		return owner;
	}

	/** 设置数据 */
	public void setOwner(Robot owner) {
		this.owner = owner;
	}

	/** 获得具體数据 */
	public RobotData getData() {
		return data;
	}

	/** 设置具體数据 */
	public void setData(RobotData data) {
		this.data = data;
	}

	/** 设置当前场景 */
	public int getCurrentMap() {
		return currentMap;
	}

	/** 设置当前场景 */
	public void setCurrentMap(int currentMap) {
		this.currentMap = currentMap;
	}

	public void SendDeviceSigninMessage(String device) {
		AppMessage msg = new AppMessage(10000);
		msg.getBuffer().writeUTF(device);
		owner.sendMessage(msg);
	}

	public void sendPassportSigninMessage(String username, String passport) {
		AppMessage msg = new AppMessage(10001);
		msg.getBuffer().writeUTF(username);
		msg.getBuffer().writeUTF(passport);
		owner.sendMessage(msg);
	}

	public void sendBattleResult(int nodeId, boolean isPass, int score) {
		AppMessage msg = new AppMessage(10004);
		msg.getBuffer().writeInt(nodeId);
		msg.getBuffer().writeBoolean(isPass);
		msg.getBuffer().writeInt(score);
		owner.sendMessage(msg);
	}
}
