package ru.job4j.habr;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.habr.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class Grabber implements Grab {
    private final Properties cfg = new Properties();
    private final static String LINK = "https://career.habr.com/vacancies/java_developer?page=";

    private static final DateTimeFormatter FORMATTER_FOR_HTML = DateTimeFormatter.ofPattern("dd MM yyyy HH:mm");

    public Store store() throws SQLException {
        return new PsqlStore(cfg);
    }

    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    public void cfg() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            cfg.load(in);
        }
    }

    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(cfg.getProperty("rabbit.interval")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public void web(Store store) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9000), 0);
        HttpHandler handler = exchange -> {
            StringBuilder builder = getHtml(store);
            byte[] bytes = builder.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().put("Content-Type", List.of("text/html", "charset=UTF-8"));
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
                os.flush();
            }
        };
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        server.createContext("/grabber", handler);
        server.setExecutor(executorService);
        server.start();
    }

    private StringBuilder getHtml(Store store) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta charset=\"UTF-8\">");
        builder.append("<title>Java Вакансии в Хабр Карьера</title>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<table>");
        builder.append("<colgroup>");
        builder.append("<col span=\"4\" style=\"background:powderblue\">");
        builder.append("</colgroup>");
        builder.append("<tr>").append("<th style=width:7%>Дата</th>").append("<th style=width:13%>Название</th>")
                .append("<th style=width:70%>Описание</th>").append("<th style=width:10%>Ссылка</th>").append("</tr>");
        for (Post post : store.getAll()) {
            builder.append("</tr>").append("<td align=\"center\">").append(post.getCreated().format(FORMATTER_FOR_HTML)).append("</td>")
                    .append("<td align=\"center\">").append(post.getTitle()).append("</td>")
                    .append("<td align=\"center\">").append(post.getDescription()).append("</td>")
                    .append("<td align=\"center\">").append("<p><a href=").append(post.getLink())
                    .append(">Ссылка на вакансию</a></p>").append("</td>")
                    .append("</tr>");
        }
        builder.append("</table>");
        builder.append("</body>");
        builder.append("</html>");
        return builder;
    }


    public static class GrabJob implements Job {

        @Override
        public void execute(JobExecutionContext context) {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> list = parse.list(LINK);
            for (Post p : list) {
                store.save(p);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Grabber grab = new Grabber();
        grab.cfg();
        Scheduler scheduler = grab.scheduler();
        Store store = grab.store();
        grab.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
        grab.web(store);
    }
}


