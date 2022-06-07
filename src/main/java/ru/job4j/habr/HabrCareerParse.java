package ru.job4j.habr;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import ru.job4j.habr.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    public static final int PAGES = 5;

    private static final Logger LOG = LoggerFactory.getLogger(HabrCareerParse.class);

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= PAGES; i++) {
            try {
                Document document = Jsoup.connect(String.format(link + "%s", i)).get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(row -> posts.add(getPost(row)));
            } catch (IOException e) {
                LOG.error(" IO исключение в list", e);
            }
        }
        return posts;
    }

    private Post getPost(Element row) {
        Element titleElement = row.select(".vacancy-card__title").first();
        assert titleElement != null;
        Element linkElement = titleElement.child(0);
        Element dateTitleElement = row.select(".vacancy-card__date").first();
        String linkToVacancy = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        assert dateTitleElement != null;
        return new Post(titleElement.text(), retrieveDescription(linkToVacancy), linkToVacancy,
                dateTimeParser.parse(dateTitleElement.child(0).attr("datetime")));
    }


    private String retrieveDescription(String link) {
        StringJoiner builder = new StringJoiner(System.lineSeparator());
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Element descriptionElement = document.selectFirst(".style-ugc");
            assert descriptionElement != null;
            builder.add(descriptionElement.text());
        } catch (IOException e) {
            LOG.error(" IO исключение в retrieveDescription", e);
        }
        return builder.toString();
    }
}


