package ru.job4j.grabber;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class SqlParseSite implements Parse {
    private List<Post> posts = new ArrayList<>();

    public List<Post> getPosts() {
        return posts;
    }

    public void pages(String link, int pages) {
        for (int i = 1; i <= pages; i++) {
            String newStr = String.format(link + "%s", i);
            parse(newStr);
        }
    }

    public void parse(String link) {
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

    @Override
    public List<Post> list(String link) {
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            String ref = href.attr("href");
            posts.add(new Post(ref));
        }
        return posts;
    }

    public Post detail(String link) {
        Post post = new Post();
        SqlRuDateTimeParser date = new SqlRuDateTimeParser();
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String text = doc.select(".msgBody").get(1).text();
        String name = doc.select(".messageHeader").get(0).text();
        String created = doc.select(".msgFooter").get(0).text();
        created = new StringBuilder(created).substring(0, created.indexOf("["));
        try {
            post = new Post(name, text, link, date.parse(created));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return post;
    }

    public static void main(String[] args) {
        SqlParseSite sql = new SqlParseSite();
        //sql.pages("https://www.sql.ru/forum/job-offers/", 5);
        //sql.detail("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        sql.list("https://www.sql.ru/forum/job-offers/5");
        for (Post post : sql.getPosts()) {
            post = sql.detail(post.getLink());
            System.out.println(post);
        }
    }
}
