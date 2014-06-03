package com.handee.utils;

import org.apache.log4j.Logger;

import java.util.concurrent.ConcurrentLinkedQueue;

class JobQueue<T extends Runnable> {
	private static final Logger _log = Logger.getLogger(JobQueue.class);
	private ConcurrentLinkedQueue<T> jobs = new ConcurrentLinkedQueue<>();
	private Thread runner;
	private T currentJob;

	//
	// private static JobQueue<> instance;
	//
	// public static final JobQueue getInstance() {
	// if (instance == null) {
	// instance = new JobQueue();
	// instance.start();
	// }
	// return instance;
	// }
	//
	// public static final void stopInstance() {
	// if (instance != null)
	// instance.stop();
	// instance = null;
	// }

	public JobQueue() {

	}

	public JobQueue(ConcurrentLinkedQueue<T> jobs) {
		if (jobs == null)
			return;
		this.jobs = jobs;
	}

	private long lastPeekTime = 0;
	private int timeout = 1000;
	private int lastQueueSize = 0;
	private int queueState = 0;

	public void peek(long currentTime) {
		if (lastPeekTime == 0) {
			lastPeekTime = currentTime;
			lastQueueSize = jobs.size();
		} else if (currentTime - lastPeekTime >= timeout) {
			// 如果队列中有JOB,检查状态
			if (jobs.size() > 0 && jobs.size() >= lastQueueSize) {
				queueState++;
			} else {// 没有.或者运行正常
				queueState = 0;
			}
			lastQueueSize = jobs.size();
			lastPeekTime = currentTime;
		}
	}

	public void addJob(T job) {
		jobs.add(job);
	}

	public boolean cancelJob(T job) {
		return jobs.remove(job);
	}

	public int getJobCount() {
		return jobs.size();
	}

	public Thread getRunner() {
		return runner;
	}

	public void start() {
		if (runner == null) {
			runner = new Runner();
			runner.setDaemon(true);
			runner.start();
		}
	}

	public void stop() {
		if (runner != null) {
			runner.interrupt();
			runner = null;
		}
		// if (currentJob != null && currentJob.getConn() != null) {
		// currentJob.getConn().close();
		// currentJob = null;
		// }
		if (currentJob != null) {
			currentJob = null;
		}
		jobs = null;
	}

	private T getNextJob() {
		while (jobs.size() <= 0 && runner != null) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		if (jobs.size() > 0) {
			return jobs.poll();
		} else {
			return null;
		}

	}

	public ConcurrentLinkedQueue<T> getJobs() {
		return jobs;
	}

	private class Runner extends Thread {
		public Runner() {
			super("JobQueueRunner Thread");
		}

		public void run() {
			while (runner == this) {
				T job = getNextJob();
				if (job == null) {
					continue;
				}
				if (runner != this) {
					break;
				}
				try {
					currentJob = job;
					job.run();
				} catch (Throwable e) {
					_log.error("JobQueue error", e);
					e.printStackTrace();
				}
			}
			runner = null;
		}
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getQueueState() {
		return queueState;
	}

	public void setQueueState(int queueState) {
		this.queueState = queueState;
	}

	public Runnable getCurrentJob() {
		return currentJob;
	}

}