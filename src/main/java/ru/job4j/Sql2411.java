package ru.job4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Sql2411 {
    public static void main(String[] args) throws IOException {


        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row2 = doc.select(".postslisttopic");

        for (Element td : row2) {
            Element el = td.parent();
            Element href = td.child(0);
            System.out.println(href.attr("href"));
            System.out.println(href.text());
            System.out.println(el.child(5).text().trim());
        }
    }

}