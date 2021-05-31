package ru.job4j.quartz;

import org.junit.Assert;
import org.junit.Test;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SqlRuDateTimeParserTest {

    @Test
    public void testYestr31May() throws ParseException {
        SqlRuDateTimeParser sq = new SqlRuDateTimeParser();
        LocalDateTime ltd = LocalDateTime.of(LocalDate.now()
                .minusDays(1), LocalTime.of(19, 40));
        Assert.assertEquals(sq.parse("вчера, 19:40"), ltd);
    }

    @Test
    public void testToday() throws ParseException {
        SqlRuDateTimeParser sq = new SqlRuDateTimeParser();
        LocalDateTime ltd = LocalDateTime
                .of(LocalDate.now(), LocalTime.of(17, 50));
        Assert.assertEquals(sq.parse("сегодня, 17:50"), ltd);
    }

    @Test
    public void testDate() throws ParseException {
        SqlRuDateTimeParser sq = new SqlRuDateTimeParser();
        LocalDateTime ltd = LocalDateTime
                .of(1997, 3, 18, 17, 50);
        Assert.assertEquals(sq.parse("18 мар 97, 17:50"), ltd);
    }

}
