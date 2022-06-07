package ru.job4j.habr.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class HabrCareerDateTimeParser implements DateTimeParser {

    @Override
    public LocalDateTime parse(String parse) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(parse);
        return zonedDateTime.toLocalDateTime();
    }
}
