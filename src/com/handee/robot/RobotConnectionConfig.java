package com.handee.robot;

/** 
 * 类说明:连接配置
 *  
 *  */
import java.util.ArrayList;
import java.util.List;

public class RobotConnectionConfig {

	/** 主机ip */
	public static String HOST = "172.20.101.123";

	/** 端口 */
	public static int PORT = 8789;

	public static int loginWaitSecMills = 10;

	public static int maxRobotAmounts = 500;

	public static int loginSuccessCount = 0;

	public static int currentConnectCount = 0;

	public static int loginFallCount = 0;

	public static int connectError = 0;

	public static int connectPerMinite = 0;

	public static int lockedCount = 0;
	public static int noAnswerCount = 0;

	public static int sendConnectCount = 0;

	public static int reConnectCount = 0;

	public static boolean running = false;

	/** 聊天消息 */
	public static List<String> talkContent = new ArrayList<String>();

	/** 置空 */
	public void clear() {
		loginSuccessCount = 0;
		currentConnectCount = 0;
		loginFallCount = 0;
		connectError = 0;
		connectPerMinite = 0;
		lockedCount = 0;
		noAnswerCount = 0;
		sendConnectCount = 0;
		reConnectCount = 0;
		running = false;
	}
}
