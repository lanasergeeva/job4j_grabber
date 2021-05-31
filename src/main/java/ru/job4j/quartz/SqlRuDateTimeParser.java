package ru.job4j.quartz;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class SqlRuDateTimeParser implements DateTimeParser {
    public String[] getMonths() {
        return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                "июл", "авг", "сен", "окт", "ноя", "дек"};
    }

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        Date date;
        LocalDateTime rsl;
        String month = "dd MMM yy HH:mm";
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        dfs.setShortMonths(getMonths());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(month, dfs);
        parse = new StringBuilder(parse).deleteCharAt(parse.indexOf(",")).toString();
        String[] array = parse.split(" ");
        if (array[0].equals("вчера")) {
            array[0] = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd MMM yy"));
            parse = array[0] + " " + array[1];
            date = simpleDateFormat.parse(parse);
            rsl = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        } else if (parse.contains("сегодня")) {
            array[0] = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yy"));
            parse = array[0] + " " + array[1];
            date = simpleDateFormat.parse(parse);
            rsl = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        } else {
            date = simpleDateFormat.parse(parse);
            rsl = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return rsl;
    }

    public static void main(String[] args) throws ParseException {
        String par = "15 июн 20, 19:23";
        SqlRuDateTimeParser sq = new SqlRuDateTimeParser();
        System.out.println(sq.parse(par));
    }
}
