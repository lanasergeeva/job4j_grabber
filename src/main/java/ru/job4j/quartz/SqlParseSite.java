package ru.job4j.quartz;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;


public class SqlParseSite {
    public void pages(String link, int pages)  {
        for (int i = 1; i <= pages; i++) {
            String newStr = String.format(link + "/" + "%s", i);
            parse(newStr);
        }
    }

    public void parse(String link)  {
        SqlRuDateTimeParser date = new SqlRuDateTimeParser();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            Elements dates = td.siblingElements();
            String dateSite = dates.get(4).text();
            try {
                System.out.println(date.parse(dateSite));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SqlParseSite sql = new SqlParseSite();
        sql.pages("https://www.sql.ru/forum/job-offers", 5);
    }
}
