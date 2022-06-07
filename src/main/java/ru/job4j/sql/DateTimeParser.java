package ru.job4j.sql;

import java.time.LocalDateTime;

public interface DateTimeParser {
    LocalDateTime parse(String parse);
}
