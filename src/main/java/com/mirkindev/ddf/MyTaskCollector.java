package com.mirkindev.ddf;

/*
 * cron4j - A pure Java cron-like scheduler
 *
 * Copyright (C) 2007-2010 Carlo Pelliccia (www.sauronsoftware.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
import it.sauronsoftware.cron4j.SchedulingPattern;
import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskCollector;
import it.sauronsoftware.cron4j.TaskTable;

/**
 * The custom TaskCollector used to retrieve the task list. This sample
 * implementation returns always the same task that the scheduler executes once
 * a minute.
 */
public class MyTaskCollector implements TaskCollector {

    @Override
    public TaskTable getTasks() {

        SchedulingPattern patternStart = new SchedulingPattern("* * * * 1,2,3,4,5,6");
        SchedulingPattern patternEnd = new SchedulingPattern("* * * * 1,2,3,4,5,6");
        TaskTable ret = new TaskTable();

        Task taskSubscribeStart = new MyTask("Start");
        Task taskSubscribeEnd = new MyTask("End");

        ret.add(patternStart, taskSubscribeStart);
        ret.add(patternEnd, taskSubscribeEnd);
        return ret;
    }
}