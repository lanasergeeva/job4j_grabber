package ru.job4j.grabber;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class SqlRuDateTimeParser implements DateTimeParser {
    private final DateFormatSymbols dfs = DateFormatSymbols.getInstance();
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("dd MMM yy HH:mm", getDfs());
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM yy");

    public static final String[] getMonths() {
        return new String[]{"янв", "фев", "мар", "апр", "май", "июн",
                "июл", "авг", "сен", "окт", "ноя", "дек"};
    }

    private DateFormatSymbols getDfs() {
        dfs.setShortMonths(getMonths());
        return dfs;
    }

    @Override
    public LocalDateTime parse(String parse) throws ParseException {
        LocalDateTime rsl;
        parse = new StringBuilder(parse).deleteCharAt(parse.indexOf(",")).toString();
        String[] array = parse.split(" ");
        if (parse.contains("вчера") || parse.contains("сегодня")) {
            array[0] = parse.contains("вчера") ? LocalDate.now().minusDays(1).format(dtf)
                    : LocalDate.now().format(dtf);
            parse = array[0] + " " + array[1];
            rsl = LocalDateTime.ofInstant(sdf.parse(parse).toInstant(), ZoneId.systemDefault());
        } else {
            rsl = LocalDateTime.ofInstant(sdf.parse(parse).toInstant(), ZoneId.systemDefault());
        }
        return rsl;
    }


    public static void main(String[] args) throws ParseException {
        String par = "сегодня, 19:23";
        SqlRuDateTimeParser sq = new SqlRuDateTimeParser();
        System.out.println(sq.parse(par));
    }
}
