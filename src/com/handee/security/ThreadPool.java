package com.handee.security;


import java.util.LinkedList;

/**
 * �̳߳�
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
	 *  �߳���
	 */
	public ThreadPool(int threadNum, int id) {
		this.threadNum = threadNum;
		this.poolID = id;
	}

	/**
	 * ��ʼ��pool
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
	 * �ر�pool
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
	 * �������
	 * @param task
	 */
	public synchronized void addTask(Runnable task) {
		if(!active){
			throw new IllegalStateException();// �̱߳������׳�IllegalStateException�쳣
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
	}
}
