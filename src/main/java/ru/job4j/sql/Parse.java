package ru.job4j.sql;
import java.util.List;

public interface Parse {
    List<Post> list(String link);

    Post detail(String link);
}
