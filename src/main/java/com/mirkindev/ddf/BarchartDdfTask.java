package com.mirkindev.ddf;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutionContext;

/**
 * Created by dimam on 30/10/2016.
 */
public class BarchartDdfTask  extends Task {

    public enum TaskOperation { START_SUBSCRIPTION, END_SUBSCRIPTION }

    private DdfplusClient client;
    private TaskOperation operation;


    public BarchartDdfTask(DdfplusClient client, TaskOperation operation){
        this.client = client;
        this.operation = operation;
    }

    @Override
    public void execute(TaskExecutionContext executor) throws RuntimeException {

        switch (this.operation){
            case START_SUBSCRIPTION:
                try {
                    client.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case END_SUBSCRIPTION:
                client.shutdown();
                break;
        }

    }
}
