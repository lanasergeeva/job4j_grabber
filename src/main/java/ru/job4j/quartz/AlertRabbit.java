package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Properties;

public class AlertRabbit {

    public static Connection initConnection(Properties settings) throws Exception {
        Class.forName(settings.getProperty("driver"));
        String url = settings.getProperty("url");
        String login = settings.getProperty("username");
        String password = settings.getProperty("password");
        return DriverManager.getConnection(url, login, password);
    }

    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(
                "./src/main/resources/rabbit.properties")) {
            properties.load(in);
        }
        return properties;
    }


    public static void main(String[] args) throws Exception {
        Properties properties = getProperties();
        try (Connection connection = initConnection(properties)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("cn", connection);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(properties.getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("cn");
            try (PreparedStatement statement = connection
                    .prepareStatement(
                            "insert into rabbit (time_connect, date_connect) values( ?, ?) ")) {
                statement.setTime(1, Time.valueOf(LocalTime.now()));
                statement.setDate(2, Date.valueOf(LocalDate.now()));
                statement.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}