package com.handee.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * 线程池
 */
public class ThreadPool {
	private int maxThreads = 15;

	private int threadNum;
	private int threadID = 1;
	private int poolID;
	private boolean active;

	private ThreadGroup pool;
	private LinkedList<Runnable> taskQueue;

	/**
	 * @param threadNum
	 *  线程数
	 */
	public ThreadPool(int threadNum, int id) {
		this.threadNum = threadNum;
		this.poolID = id;
	}

	/**
	 * 初始化pool
	 */
	public void init() {
		active = true;
		if (threadNum > maxThreads)
			threadNum = maxThreads;
		if(threadNum <= 0)
			threadNum = maxThreads;
		taskQueue = new LinkedList<Runnable>();
		pool = new ThreadGroup("ThreadPool" + poolID);
		for (int i = 0; i < threadNum; i++) {
			new TaskThread().start();
		}
	}

	/**
	 * 关闭pool
	 */
	public synchronized void destroy() {
		if(pool == null)
			return;
		active = false;
		Thread[] threads = new Thread[pool.activeCount()];
		pool.enumerate(threads);
		TaskThread taskThread;
		for (int i = 0; i < threads.length; i++) {
			taskThread = (TaskThread)threads[i];
			taskThread.stopThread();
		}
		notifyAll();
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException ex) {
			}
		}
	}

	/**
	 * 添加任务
	 * @param task
	 */
	public synchronized void addTask(Runnable task) {
		if(!active){
			throw new IllegalStateException();// 线程被关则抛出IllegalStateException异常
		}
		
		if(task != null){
			taskQueue.add(task);
			notify();
		}
	}

	private synchronized Runnable getTask() throws InterruptedException {
		while (taskQueue.size() == 0) {
			if (!active)
				return null;
			wait();
		}

		return (Runnable) taskQueue.removeFirst();
	}

	private class TaskThread extends Thread {
		private boolean running = true;

		public TaskThread() {
			super(pool, "TaskThread" + (threadID++));
		}

		public void run() {
			while (running) {
				Runnable task = null;
				try {
					task = getTask();
				} catch (InterruptedException e) {

				}
				if (task == null)
					return;
				try {
					task.run();
				} catch (Throwable e) {
					pool.uncaughtException(this, e);
				}
			}
		}

		public void stopThread() {
			running = false;
		}

		public String toString() {
			return "[" + getId() + "][" + getName() + "][" + isAlive() + "]["
					+ getState() + "]";
		}
	}

	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	public int getMaxThreads() {
		return this.maxThreads;
	}

	public String toString() {
		String description = pool.getName() + "\r\n";
		Thread[] threads = new Thread[pool.activeCount()];
		pool.enumerate(threads);
		for (int i = 0; i < threads.length; i++) {
			description += threads[i].toString() + "\r\n";
		}

		return description;
	}

	public static void main(String[] args) throws Exception {
		ThreadPool pool = new ThreadPool(10, 333);
		pool.init();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.println("请输入指令----");
			String cmd = in.readLine();
			if (cmd.equals("list")) {
				System.out.println(pool);
			} else if (cmd.equals("exit")) {
				pool.destroy();
				System.out.println(pool);
			} else if (cmd.equals("notify")) {
				break;
			} else if (cmd.equals("task")){
				for (int i = 0; i < 20; i++) {
					pool.addTask(createTask(i));
				}
			}
		}
	}
	
	/**
	 * 一个简单的任务(打印ID)
	 */
	private static Runnable createTask(final int taskID) {
		return new Runnable() {
			public void run() {
				System.out.println("Task " + taskID + ": start");

				// 增加耗时
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
				}

				System.out.println("Task " + taskID + ": end");
			}
		};
	}
}
