package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import java.io.FileInputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Properties;

public class AlertRabbit {

    public static Connection initConnection() throws Exception {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream("./src/main/resources/rabbit.properties")) {
            properties.load(in);
        }
        Class.forName(properties.getProperty("driver"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url, login, password);
    }

    public static void main(String[] args) throws Exception {
        Connection connection = initConnection();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(10)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
            try (PreparedStatement statement = initConnection()
                    .prepareStatement("insert into rabbit (time_connect, date_connect) values ( ?, ? )")) {
                statement.setTime(1, Time.valueOf(LocalTime.now()));
                statement.setDate(2, Date.valueOf(LocalDate.now()));
                statement.execute();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
