package com.handee.utils;

import com.handee.job.JobProcessCallback;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class JobQueueOB implements Runnable {
    private static final Logger logger = Logger.getLogger(JobQueueOB.class);
    private static JobQueueOB instance;

    public static int JOB_QUEUE_SIZE = 6;
    public static int JOB_QUEUE_BLOCK_STATE = 10;
    public static int JOB_QUEUE_TIMEOUT = 1000;
    private boolean isRunning;
    private JobProcessCallback<Void> afterProcessAllJob;
    private ArrayList<JobQueue<Runnable>> jobQueueGroup = new ArrayList<>();

    public static JobQueueOB getInstance() {
        if (instance == null) {
            instance = new JobQueueOB(JOB_QUEUE_SIZE);
        }
        return instance;
    }

    private JobQueueOB(int size) {
        jobQueueGroup = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            createJobQueue(i, null);
            setQueueTimeout(i, JOB_QUEUE_TIMEOUT);
        }

        // new Thread(this, "Timeout Detect Thread").start();
        // System.out.println("JobQueueOB timeout detect thread started.");
    }

    public void setQueueTimeout(int index, int timeout) {
        if (index < 0 || index >= jobQueueGroup.size())
            return;
        jobQueueGroup.get(index).setTimeout(timeout);
    }

    public void resetAllQueue() {
        for (int i = 0; i < jobQueueGroup.size(); i++) {
            reset(i, false);
        }
    }

    public void run() {
        isRunning = true;
        while (isRunning) {
            Utils.sleep(50);
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < jobQueueGroup.size(); i++) {
                jobQueueGroup.get(i).peek(currentTime);
                if (jobQueueGroup.get(i).getQueueState() > JobQueueOB.JOB_QUEUE_BLOCK_STATE) {
                    logger.warn("队列繁忙[" + i + "][" + jobQueueGroup.get(i).getCurrentJob().getClass() + "]:" + jobQueueGroup.get(i).getJobCount());
                    reset(i, true);
                }
            }
        }
        for (JobQueue<Runnable> queue : jobQueueGroup) {
            if (queue != null) {
                queue.stop();
            }
        }
        jobQueueGroup.clear();
    }

    /**
     * 添加一个无返回值的Job
     *
     * @param index 索引
     * @param job   Job
     */
    public void addJob(int index, Runnable job) {
        if (index < 0 || index >= jobQueueGroup.size() || jobQueueGroup.get(index) == null) {
            logger.error("[addJob(int index, Runnable job)] add job to an error queue index :: " + index);
            return;
        }
        jobQueueGroup.get(index).addJob(job);
    }

    /**
     * 添加一个无返回值的Job.
     * <p/>
     * 随机将些job添加到队列池中的某一队列。
     *
     * @param job job
     */
    public void addJob(Runnable job) {
        if (job == null) {
            return;
        }
        int index = Math.abs(job.hashCode() % getOBSize());
        addJob(index, job);
    }

    /**
     * 添加一个有返回值的Job.
     * <p/>
     * 随机将些job添加到队列池中的某一队列。
     *
     * @param job job
     * @return RunnableFuture<T>
     */
    public <T> RunnableFuture<T> addJob(Callable<T> job) {
        if (job == null) {
            return null;
        }
        int index = Math.abs(job.hashCode() % getOBSize());
        return addJob(index, job);
    }

    /**
     * 添加一个有返回值的Job
     *
     * @param index 索引
     * @param job   Job
     * @param <T>   返回值类型
     * @return RunnableFuture<T>
     */
    public <T> RunnableFuture<T> addJob(int index, Callable<T> job) {
        if (index < 0 || index >= jobQueueGroup.size() || jobQueueGroup.get(index) == null) {
            logger.error("[addJob(int index, Callable<T> callable)] add job to an error queue index :: " + index);
            return null;
        }
        RunnableFuture<T> returnVal = new FutureTask<>(job);
        addJob(index, returnVal);
        return returnVal;
    }

    private void createJobQueue(int index, ConcurrentLinkedQueue<Runnable> jobs) {
        JobQueue<Runnable> jobQueue = new JobQueue<>(jobs);
        jobQueueGroup.add(jobQueue);
        jobQueue.start();
        logger.info("jboQueue init:" + Utils.getCurrentTime() + " index=" + index);
    }

    public JobQueue<Runnable> getJobQueue(int index) {
        if (index < 0 || index >= jobQueueGroup.size())
            return null;
        return jobQueueGroup.get(index);
    }

    public void stop() {
        isRunning = false;
        afterProcessAllJob = null;
    }

    public int getOBSize() {
        return jobQueueGroup.size();
    }

    public int getAllJobSize() {
        int size = 0;
        for (JobQueue<Runnable> queue : jobQueueGroup) {
            size += queue.getJobCount();
        }
        return size;
    }

    public void reset(int index, boolean isCopyingJob) {
        JobQueue<Runnable> jobQueue = jobQueueGroup.get(index);
        ConcurrentLinkedQueue<Runnable> jobs = new ConcurrentLinkedQueue<>();
        jobs.addAll(jobQueue.getJobs());
        for (Runnable job : jobs) {
            System.out.println("leftjob:" + job);
        }
        new Thread(new StopJobQueue(jobQueue)).start();
        if (isCopyingJob) {
            new Thread(new ResetJobQueue(index, jobs)).start();
        } else {
            new Thread(new ResetJobQueue(index, null)).start();
            jobs.clear();
        }
    }

    public void resetJobQueue(int index, ConcurrentLinkedQueue<Runnable> jobs) {

        JobQueue<Runnable> jobQueue = new JobQueue<>(jobs);

        jobQueueGroup.remove(index);

        jobQueueGroup.add(index, jobQueue);

        jobQueue.start();
    }

    /**
     * 启动检测所有Job是否执行完成。
     *
     * @param afterCheck Job执行完成之后操作
     */
    public void startCheckJob(JobProcessCallback<Void> afterCheck) {
        // 所有JOB执行完成之后回调。
        this.afterProcessAllJob = afterCheck;
        // 启用JOB检测线程
        new Thread(new CheckJob(), "Thread Check All Job Run Finished").start();
    }

    private class CheckJob implements Runnable {
        private long preCheckTime = 0;
        private boolean allJobProcessed = false;

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            // 如果Job没有处理完
            while (!allJobProcessed) {
                // 检测间隔时间
                if (System.currentTimeMillis() - preCheckTime >= 1000) {
                    // 重置检测时间
                    preCheckTime = System.currentTimeMillis();
                    // 剩余多少JOB在Job池
                    int leftJobSize = JobQueueOB.getInstance().getAllJobSize();
                    // 如果没有
                    if (leftJobSize <= 0) {
                        // job执行完成
                        allJobProcessed = true;
                    }
                }
                // 暂停
                Utils.sleep(50);
            }
            // 所有Job执行完毕，
            if (afterProcessAllJob != null) {
                afterProcessAllJob.call(null);
            }
        }
    }

    private class StopJobQueue implements Runnable {
        private JobQueue<Runnable> jobQueue;

        public StopJobQueue(JobQueue<Runnable> jobQueue) {
            this.jobQueue = jobQueue;
        }

        public void run() {
            StackTraceElement[] stacks = jobQueue.getRunner().getStackTrace();

            for (StackTraceElement stack : stacks) {
                System.out.println(stack);
            }
            jobQueue.stop();
        }
    }

    private class ResetJobQueue implements Runnable {
        private int index;
        private ConcurrentLinkedQueue<Runnable> jobs;

        public ResetJobQueue(int index, ConcurrentLinkedQueue<Runnable> jobs) {
            this.index = index;
            this.jobs = jobs;
        }

        public void run() {
            resetJobQueue(index, jobs);
        }
    }
}
