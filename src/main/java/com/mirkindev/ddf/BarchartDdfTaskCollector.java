package com.mirkindev.ddf;

import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskTable;

/**
 * Created by dimam on 30/10/2016.
 */
public class BarchartDdfTaskCollector implements TaskCollector {

    BarchartQuoteService client;

    BarchartDdfTaskCollector(BarchartQuoteService client){
        this.client = client;
    }

    @Override
    public TaskTable getTasks() {

        SchedulingPattern patternStart1 = new SchedulingPattern("00 07 * * 0,1,2,3,4,5");
        SchedulingPattern patternEnd1 = new SchedulingPattern("45 07 * * 0,1,2,3,4,5");
        SchedulingPattern patternStart2 = new SchedulingPattern("30 08 * * 0,1,2,3,4,5");
        SchedulingPattern patternEnd2 = new SchedulingPattern("00 12 * * 0,1,2,3,4,5");
        TaskTable ret = new TaskTable();

        ret.add(patternStart1, new BarchartDdfTask(client,BarchartDdfTask.TaskOperation.START_SUBSCRIPTION));
        ret.add(patternEnd1, new BarchartDdfTask(client, BarchartDdfTask.TaskOperation.END_SUBSCRIPTION));
        ret.add(patternStart2, new BarchartDdfTask(client,BarchartDdfTask.TaskOperation.START_SUBSCRIPTION));
        ret.add(patternEnd2, new BarchartDdfTask(client, BarchartDdfTask.TaskOperation.END_SUBSCRIPTION));

        return ret;
    }
}
