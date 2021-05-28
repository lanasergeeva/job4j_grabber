package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class AlertRabbit {
    private static int interval;

    public static void main(String[] args) throws Exception {
        load();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception se) {
            se.printStackTrace();
        }
    }

    public static int load() {
        try (BufferedReader in = new BufferedReader(new FileReader("C:\\projects\\job4j_grabber\\src\\main\\resources\\rabbits.properties"))) {
            in.lines()
                    .forEach(line -> {
                        String[] array = line.split("=", 2);
                        if (array[1].isEmpty()) {
                            throw new IllegalArgumentException();
                        } else {
                            interval = Integer.parseInt(array[1]);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interval;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here ...");
        }
    }
}
