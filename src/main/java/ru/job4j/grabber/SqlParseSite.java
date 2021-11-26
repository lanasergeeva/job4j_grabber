package ru.job4j.grabber;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.SqlRuParse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlParseSite implements Parse {

    private static final Logger LOG = LogManager.getLogger(SqlRuParse.class.getName());

    private final DateTimeParser dateTimeParser;

    public SqlParseSite(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    private List<Post> posts = new ArrayList<>();

    public List<Post> getPosts() {
        return posts;
    }


    @Override
    public List<Post> list(String link) {
        Document doc = null;
        for (int i = 1; i <= 5; i++) {
            try {
                doc = Jsoup.connect(String.format(link + "%s", i)).get();
            } catch (IOException e) {
                LOG.error(" IO исключение в list", e);
            }
            Elements row = Objects.requireNonNull(doc).select(".postslisttopic");
            for (Element td : row) {
                Element href = td.child(0);
                String ref = href.attr("href");
                Post post = detail(ref);
                if (post.getName().toLowerCase().contains("java")
                        && !post.getName().toLowerCase().contains("javascript")) {
                    posts.add(post);
                }
            }
        }
        return posts;
    }

    public Post detail(String link) {
        Post post;
        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            LOG.error(" IO исключение в detail", e);
        }
        String text = Objects.requireNonNull(doc).select(".msgBody").get(1).text();
        String name = doc.select(".messageHeader").get(0).ownText();
        String created = doc.select(".msgFooter").get(0).text();
        created = new StringBuilder(created).substring(0, created.indexOf("["));
        post = new Post(name, text, link, dateTimeParser.parse(created));
        return post;
    }

    public static void main(String[] args) throws ParseException, IOException {
        DateTimeParser dt = new SqlRuDateTimeParser();
        SqlParseSite sql = new SqlParseSite(dt);
        List<Post> l = sql.list("https://www.sql.ru/forum/job-offers");
        System.out.println(sql.detail("https://www.sql.ru/forum/1340389/ishhu-team-lead-java"));
    }
}

