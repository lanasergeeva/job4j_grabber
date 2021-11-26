package ru.job4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.DateTimeParser;
import ru.job4j.grabber.Parse;
import ru.job4j.grabber.Post;
import ru.job4j.grabber.SqlRuDateTimeParser;


import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class SqlRuParse {

    public Post getPost(String link) {
        Post result;
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element row = doc.select(".msgTable").get(0);
        String title = row.child(0).child(0).child(0).text();
        String description = row.child(0).child(1).child(1).text();
        String date = row.child(0).child(2).child(0).text().substring(0, 16);
        SqlRuDateTimeParser dateTimeParser = new SqlRuDateTimeParser();
        LocalDateTime created = dateTimeParser.parse(date);
        result = new Post(0, title, link, description, created);
        return result;
    }

    public static void main(String[] args)  {
       /* SqlRuParse arse = new SqlRuParse();
        System.out.println(arse.getPost("https://www.sql.ru/forum/1339644/vakansiya-programmista-sql-udalenka-ofis-v-msk-zp-120-000-200-000"));*/
        Document doc = null;
        try {
            doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = doc.select(".postslisttopic");

           Elements elements = doc.select(".msgTable");

            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                //System.out.println(td.parent().child(1).text());
                System.out.println(href.text());
                //System.out.println("href.text()");
                Element date = td.parent().child(5);
                System.out.println(date.text());
            }
        }
    }


class SqlRuDateTimeParser2 implements DateTimeParser {

    private static final String[] SHORT_MONTH = {
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"};
    private static final String TODAY = "сегодня";
    private static final String YESTERDAY = "вчера";
    private static final Integer ONE_DAY = 1;
    private static final Locale LOCALE = new Locale("ru");
    private static final DateFormatSymbols DATE_FORMAT_SYMBOLS = DateFormatSymbols.getInstance(LOCALE);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yy',' HH:mm", LOCALE);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT_SHORT = new SimpleDateFormat("dd MMM yy", LOCALE);

    public SqlRuDateTimeParser2() {
        DATE_FORMAT_SYMBOLS.setShortMonths(SHORT_MONTH);
        SIMPLE_DATE_FORMAT.setDateFormatSymbols(DATE_FORMAT_SYMBOLS);
        SIMPLE_DATE_FORMAT_SHORT.setDateFormatSymbols(DATE_FORMAT_SYMBOLS);
    }

    @Override
    public LocalDateTime parse(String parse)  {
        String day = replace(parse);
        try {
            return SIMPLE_DATE_FORMAT
                    .parse(day).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String replace(String str) {
        if (str.contains(TODAY)) {
            String format = SIMPLE_DATE_FORMAT_SHORT.format(new Date());
            str = str.replace(TODAY, format);
        }
        if (str.contains(YESTERDAY)) {
            String format = SIMPLE_DATE_FORMAT_SHORT
                    .format(Date.from((LocalDate.now().minusDays(ONE_DAY)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant())));
            str = str.replace(YESTERDAY, format);
        }
        return str;
    }

    public static void main(String[] args) throws ParseException {
        SqlRuDateTimeParser2 sqlP = new SqlRuDateTimeParser2();
        System.out.println(sqlP.parse("20 янв 05, 22:30"));
        System.out.println(sqlP.parse("вчера, 23:30"));
        System.out.println(sqlP.parse("сегодня, 04:30"));
    }
}

class SqlRuParse28 implements Parse {

    private final DateTimeParser dateTimeParser;

    public SqlRuParse28(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    /*public static void main(String[] args) throws Exception {
        String urlPost = "https://www.sql.ru/forum/1325330/"
                + "lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t";
        for (int i = 1; i <= 5; i++) {
            String url = "https://www.sql.ru/forum/job-offers/";
            Document doc = Jsoup.connect(url + i).get();
            Elements row = doc.select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                Element date = td.parent().child(5);
                System.out.println(date.text());
            }
            System.out.println("\n" + "---------------------------- End page "
                    + i + " ----------------------------------" + "\n");
        }
        postParse(urlPost);
    }*/

    /**
     * Парсит один пост, получая при этом описание поста и дату создания.
     *
     * @param url ссылка на конкретный пост
     */

    public static void postParse(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element message = doc.select(".msgBody").get(1);
        Elements date = doc.select(".msgFooter");
        String mesText = message.text();
        String dateText = date.first().ownText().replace(" [] |", "");
        System.out.println("---------------------------- postParse "
                + "----------------------------------");
        System.out.println(mesText);
        System.out.println(dateText);
    }

    /**
     * Парсит список всех постов
     *
     * @param link ссылка на страницу
     * @return возвращает список всех постов со страницы
     */

    @Override
    public List<Post> list(String link)  {
        List<Post> postList = new ArrayList<>();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            String attr = href.attr("href");
            postList.add(detail(attr));
        }
        return postList;
    }

    /**
     * Загружает все детали одного поста (имя, описание, дату создания, ссылку на пост).
     *
     * @param link ссылка на страницу
     * @return модель с данными.
     */

    @Override
    public Post detail(String link)  {
        Post post = new Post();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements postMessageHeader = doc.select(".messageHeader");
        Element postDescription = doc.select(".msgBody").get(1);
        Elements postDateCreated = doc.select(".msgFooter");
        String title = postMessageHeader.first().text().trim();
        String description = postDescription.ownText().trim();
        String dateCreated = postDateCreated.first().ownText().replace(" [] |", "");
        LocalDateTime parseDate = dateTimeParser.parse(dateCreated);
        post.setName(title);
        post.setText(description);
        post.setLink(link);
        post.setCreated(parseDate);
        return post;
    }
}