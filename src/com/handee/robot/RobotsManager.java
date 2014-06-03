package com.handee.robot;

import com.handee.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotsManager {
    /**
     * 日志信息
     */
    private static final Logger log = LoggerFactory.getLogger(RobotsManager.class);
    /**
     * 最大人数限制
     */
    private static final int MAX_SIZE_PER_THREAD = 1000;
    /**
     * 模拟玩家集合
     */
    List<GroupManager> groupManagers = new ArrayList<>(20);

    /**
     * 构造(用户名,数量)
     */
    public RobotsManager(int maxRobotAmounts) {
        for (int i = 0; i < maxRobotAmounts; i++) {
            String[] str = new String[2];
            if (i > 0)
                str[0] = "ayoo1";
            else
                str[0] = "64f934b5-d265-4aca-97ae-8bfc91368sf1";
            str[1] = "459945146";
            try {
                Robot robot = new Robot(str[0], str[1]);
                RobotData data = new RobotData();
                ActManager act = new ActManager(data);
                act.setOwner(robot);
                act.setData(data);
                robot.setActManager(act);
                robot.start();
                addRobot(robot);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Utils.sleep(RobotConnectionConfig.loginWaitSecMills);
            RobotConnectionConfig.running = true;
        }
    }

    /**
     * 添加模拟玩家到非满集合
     */
    private void addRobot(Robot r) {
        for (GroupManager gm : groupManagers) {
            if (!gm.isFull()) {
                gm.addRobot(r);
                return;
            }
        }
        GroupManager gm = new GroupManager();
        groupManagers.add(gm);
        gm.addRobot(r);
    }

    /**
     * 清除数据
     */
    public void clear() {
        groupManagers.clear();
    }

    /**
     * 机器人维护线程
     */
    class GroupManager extends Thread {

        /**
         * 玩家集合
         */
        private List<Robot> list = new ArrayList<>(1000);

        public GroupManager() {
            start();
        }

        public void run() {
            long currentTime = System.currentTimeMillis();
            while (true) {
                long timeMillis = System.currentTimeMillis();
                if (timeMillis - currentTime > 1000) {
                    for (Robot r : list) {
                        try {
                            if (r.isActive())
                                r.update();
                        } catch (Throwable e) {
                            log.error("Robot updating error; " + r.getAccount(), e);
                        }
                    }
                    currentTime = System.currentTimeMillis();
                }
            }
        }

        /**
         * 添加模拟玩家
         */
        public void addRobot(Robot r) {
            list.add(r);
        }

        /**
         * 删除模拟玩家
         */
        public boolean removeRobot(Robot r) {
            return list.remove(r);
        }

        /**
         * 是否到最大限制数量
         */
        public boolean isFull() {
            return list.size() >= RobotsManager.MAX_SIZE_PER_THREAD;
        }

    }

    public static void main(String args[]) {
        // String robotName = args[0];
        // int robotNumber = MathTools.getInteger(args[1]);
        new RobotsManager(2);
        RobotConnectionConfig.talkContent.add("别再自己摸索，问路才不会迷路。 过去不等于未来；没有失败，只有暂时停止成功!");
        RobotConnectionConfig.talkContent.add("明天是世上增值最快的一块土地，因它充满了希望!");
        RobotConnectionConfig.talkContent.add("没有一种不通过蔑视、忍受和奋斗就可以征服的命运");
        RobotConnectionConfig.talkContent.add("人生的价值，即以其人对于当代所做的工作为尺度!");
        RobotConnectionConfig.talkContent.add("万事须求脱俗 品味当然更须脱俗,不愿媚俗的人可谓明智极了!");
        RobotConnectionConfig.talkContent.add("人类的幸福和欢乐在于奋斗，而最有价值的是为理想而奋斗!");
        RobotConnectionConfig.talkContent.add("更新你的辉煌 这是凤凰再生的秘诀");
        RobotConnectionConfig.talkContent.add("为人类的幸福而劳动，这是多麽壮丽的事业，这个目的有多麽伟大! ");
        RobotConnectionConfig.talkContent.add("要坚持真理---不论在哪里也不要动! ");
        RobotConnectionConfig.talkContent.add("人类先知最富睿智的箴言 为了与你一起来探讨这个重大的社会问题，我们不妨先来看看人类的一些先知们在这个问题上最富睿智的箴言");
    }
}
