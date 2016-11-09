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


import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TaskCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Properties;
import java.util.TimeZone;

/**
 * This listener starts a scheduler bounded to the web application: the
 * scheduler is started when the application is started, and the scheduler is
 * stopped when the application is destroyed. The scheduler uses a custom
 * TaskCollector to retrieve, once a minute, its job list. Moreover the
 * scheduler is registered on the application context, in a attribute named
 * according to the value of the {@link Constants#SCHEDULER} constant.
 */
public class SchedulerServletContextListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(SchedulerServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        //0. Create DdfClient
        try {
            Properties p = new Properties();
            p.load(context.getResourceAsStream("/WEB-INF/client.properties"));

            BarchartQuoteService client = BarchartQuoteService.createClient(p);
            context.setAttribute(Constants.DDF_CLIENT, client);

            TimeZone tz = TimeZone.getTimeZone("CST");
            log.debug("Setting Timezone :" + tz.toString());

            // 1. Creates the scheduler.
            Scheduler scheduler = new Scheduler();
            scheduler.setTimeZone(TimeZone.getTimeZone("CST"));

            // 2. Registers a custom task collector.
            TaskCollector collector = new BarchartDdfTaskCollector(client);
            scheduler.addTaskCollector(collector);
            // 3. Starts the scheduler.
            scheduler.start();
            // 4. Registers the scheduler.
            context.setAttribute(Constants.SCHEDULER, scheduler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {

        ServletContext context = event.getServletContext();
        // 1. Retrieves the scheduler from the context.
        Scheduler scheduler = (Scheduler) context.getAttribute(Constants.SCHEDULER);
        // 2. Removes the scheduler from the context.
        context.removeAttribute(Constants.SCHEDULER);
        // 3. Stops the scheduler.
        scheduler.stop();

        BarchartQuoteService client = (BarchartQuoteService)context.getAttribute(Constants.DDF_CLIENT);
        client.shutdown();
    }

}