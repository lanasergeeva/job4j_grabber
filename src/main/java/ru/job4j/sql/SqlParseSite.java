package ru.job4j.sql;

import org.apache.log4j.LogManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.habr.HabrCareerParse;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SqlParseSite implements Parse {

    private static final Logger LOG = LoggerFactory.getLogger(SqlParseSite.class);

    public static final int PAGES = 5;

    private final DateTimeParser dateTimeParser;

    public SqlParseSite(DateTimeParser dateTimeParser) {

        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        Document doc = null;
        for (int i = 1; i <= PAGES; i++) {
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
}

