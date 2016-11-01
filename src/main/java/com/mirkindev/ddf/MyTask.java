package com.mirkindev.ddf;


import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyTask extends Task {

    private static final Logger log = LoggerFactory.getLogger(MyTask.class);

    String msg;

    public MyTask(String msg){
        this.msg = msg;
    }

    public void execute(TaskExecutionContext executor) throws RuntimeException {
      log.debug(msg);
    }

}
