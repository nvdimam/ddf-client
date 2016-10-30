package com.mirkindev.ddf;

import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskTable;

/**
 * Created by dimam on 30/10/2016.
 */
public class BarchartDdfTaskCollector implements TaskCollector {

    @Override
    public TaskTable getTasks() {

        SchedulingPattern patternStart = new SchedulingPattern("00 13 * * 1,2,3,4,5,6");
        SchedulingPattern patternEnd = new SchedulingPattern("00 17 * * 1,2,3,4,5,6");
        TaskTable ret = new TaskTable();

        Task taskSubscribeStart = new MyTask();
        Task taskSubscribeEnd = new MyTask();

        ret.add(patternStart, taskSubscribeStart);
        ret.add(patternEnd, taskSubscribeEnd);
        return ret;
    }
}
