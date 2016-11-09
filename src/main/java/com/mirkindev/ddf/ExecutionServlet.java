package com.mirkindev.ddf;

import com.google.gson.Gson;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.TaskExecutor;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet enables the user to view and control any ongoing task execution.
 * The HTML layout is generated calling the /WEB-INF/ongoing.jsp page.
 */
public class ExecutionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Retrieves the servlet context.
        ServletContext context = getServletContext();

        BarchartQuoteService service = (BarchartQuoteService)context.getAttribute(Constants.DDF_CLIENT);
        QuoteServiceInfo info = new QuoteServiceInfo(service);

        // Retrieves the scheduler.
        //Scheduler scheduler = (Scheduler) context.getAttribute(Constants.SCHEDULER);
        // Retrieves the executors.
        /*
        TaskExecutor[] executors = scheduler.getExecutingTasks();
        // Registers the executors in the request.
        req.setAttribute("executors", executors);
        // Action requested?
        String action = req.getParameter("action");
        if ("pause".equals(action)) {
            String id = req.getParameter("id");
            TaskExecutor executor = find(executors, id);
            if (executor != null && executor.isAlive() && !executor.isStopped()
                    && executor.canBePaused() && !executor.isPaused()) {
                executor.pause();
            }
        } else if ("resume".equals(action)) {
            String id = req.getParameter("id");
            TaskExecutor executor = find(executors, id);
            if (executor != null && executor.isAlive() && !executor.isStopped()
                    && executor.canBePaused() && executor.isPaused()) {
                executor.resume();
            }
        } else if ("stop".equals(action)) {
            String id = req.getParameter("id");
            TaskExecutor executor = find(executors, id);
            if (executor != null && executor.isAlive()
                    && executor.canBeStopped() && !executor.isStopped()) {
                executor.stop();
            }
        }*/
        // Layout.
        //String page = "/index.html";
        //RequestDispatcher dispatcher = req.getRequestDispatcher(page);
        //dispatcher.include(req, resp);

        //request.setAttribute("data", data);
        //req.getRequestDispatcher("/index.html").forward(req, resp);

        String action = req.getParameter("op");
        if("start".equals(action)){
            try {
                service.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if("stop".equals(action)){
            service.shutdown();
        }

        String json = new Gson().toJson(info);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    /*
    private TaskExecutor find(TaskExecutor[] executors, String id) {
        if (id == null) {
            return null;
        }
        for (int i = 0; i < executors.length; i++) {
            String aux = executors[i].getGuid();
            if (aux.equals(id)) {
                return executors[i];
            }
        }
        return null;
    }*/

}
