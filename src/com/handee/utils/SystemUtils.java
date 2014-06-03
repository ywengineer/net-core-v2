/**
 * 
 */
package com.handee.utils;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author wang
 * 
 */
public class SystemUtils {

	public static void main(String[] args) {
		printSection("cluster member information");
		printSection("[END]");
	}

	/**
	 * 输出字符串
	 * 
	 * @param s
	 */
	public static void printSection(String s) {
		StringBuilder sb = new StringBuilder(" { " + s + " } ");
		int len = 160 - sb.length();
		int middle = len / 2;
		while (len > 0) {
			if (len > middle) {
				sb.append('-');
			} else {
				sb.insert(0, '-');
			}
			len--;
		}
		System.out.println(sb.toString());
	}

	/**
	 * 返回32位唯一标识的ID
	 * 
	 * @return 32位唯一标识的ID
	 */
	public static String getUUId() {

		String s = UUID.randomUUID().toString().replaceAll("-", "");

		return s;
	}

	public static int getAvailableProcessors() {
		Runtime rt = Runtime.getRuntime();
		return rt.availableProcessors();
	}

	public static String getOSName() {
		return System.getProperty("os.name");
	}

	public static String getOSVersion() {
		return System.getProperty("os.version");
	}

	public static String getOSArch() {
		return System.getProperty("os.arch");
	}

	public static List<String> getStats() {
		List<String> list = new ArrayList<String>();

		list.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date()));
		list.add("");
		list.add("## Java Platform Information ##");
		list.add("Java Runtime Name: " + System.getProperty("java.runtime.name"));
		list.add("Java Version: " + System.getProperty("java.version"));
		list.add("Java Class Version: " + System.getProperty("java.class.version"));
		list.add("");
		list.add("## Virtual Machine Information ##");
		list.add("VM Name: " + System.getProperty("java.vm.name"));
		list.add("VM Version: " + System.getProperty("java.vm.version"));
		list.add("VM Vendor: " + System.getProperty("java.vm.vendor"));
		list.add("VM Info: " + System.getProperty("java.vm.info"));
		list.add("");
		list.add("## OS Information ##");
		list.add("Name: " + System.getProperty("os.name"));
		list.add("Architeture: " + System.getProperty("os.arch"));
		list.add("Version: " + System.getProperty("os.version"));
		list.add("");
		list.add("## Runtime Information ##");
		list.add("CPU Count: " + Runtime.getRuntime().availableProcessors());
		list.add("");
		for (String line : getMemoryUsageStatistics())
			list.add(line);
		list.add("");
		return list;
	}

	public static List<String> getStats(Thread t) {
		List<String> list = new ArrayList<String>();

		list.add(t.toString() + " - ID: " + t.getId());
		list.add(" * State: " + t.getState());
		list.add(" * Alive: " + t.isAlive());
		list.add(" * Daemon: " + t.isDaemon());
		list.add(" * Interrupted: " + t.isInterrupted());
		for (ThreadInfo info : ManagementFactory.getThreadMXBean().getThreadInfo(new long[] { t.getId() }, true, true)) {
			for (MonitorInfo monitorInfo : info.getLockedMonitors()) {
				list.add("==========");
				list.add(" * Locked monitor: " + monitorInfo);
				list.add("\t[" + monitorInfo.getLockedStackDepth() + ".]: at " + monitorInfo.getLockedStackFrame());
			}

			for (LockInfo lockInfo : info.getLockedSynchronizers()) {
				list.add("==========");
				list.add(" * Locked synchronizer: " + lockInfo);
			}

			list.add("==========");
			for (StackTraceElement trace : info.getStackTrace())
				list.add("\tat " + trace);
		}

		return list;
	}

	public static String[] getMemoryUsageStatistics() {
		double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is
																// the upper
																// limit the jvm
																// can use
		double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory
																		// the
																		// size
																		// of
																		// the
																		// current
																		// allocation
																		// pool
		double nonAllocated = max - allocated; // non allocated memory till jvm
												// limit
		double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory
																	// the
																	// unused
																	// memory in
																	// the
																	// allocation
																	// pool
		double used = allocated - cached; // really used memory
		double useable = max - used; // allocated, but non-used and
										// non-allocated memory

		SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");
		DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		DecimalFormat df2 = new DecimalFormat(" # 'KB'");

		return new String[] {
				"+----",// ...
				"| Global Memory Informations at " + sdf.format(new Date()) + ":", // ...
				"|    |", // ...
				"| Allowed Memory:" + df2.format(max), "|    |= Allocated Memory:" + df2.format(allocated) + df.format(allocated / max * 100),
				"|    |= Non-Allocated Memory:" + df2.format(nonAllocated) + df.format(nonAllocated / max * 100), "| Allocated Memory:" + df2.format(allocated),
				"|    |= Used Memory:" + df2.format(used) + df.format(used / max * 100), "|    |= Unused (cached) Memory:" + df2.format(cached) + df.format(cached / max * 100),
				"| Useable Memory:" + df2.format(useable) + df.format(useable / max * 100), // ...
				"+----" };
	}
}
