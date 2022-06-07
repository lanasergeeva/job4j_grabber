package ru.job4j.habr;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.nullValue;

public class PsqlStoreTest {

    private static Connection connection;


    @BeforeClass
    public static void initConnection() {
        try (InputStream in =
                     PsqlStoreTest.class.getClassLoader().getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("delete from post")) {
            statement.execute();
        }
    }

    @Test
    public void whenFindAll() {
        PsqlStore store = new PsqlStore(connection);
        LocalDateTime date = LocalDateTime.now();
        Post first = store.save(new Post("Java Junior", "Best",
                "https://career.habr.com/vacancies/1000101085", date));
        Post second = store.save(new Post("Mobile QA", "Description",
                "https://career.habr.com/vacancies/1000105676", date));
        assertThat(store.getAll(), is(List.of(first, second)));
    }

    @Test
    public void whenFindById() {
        PsqlStore store = new PsqlStore(connection);
        LocalDateTime date = LocalDateTime.now();
        Post first = store.save(new Post("Java Junior", "Description",
                "https://career.habr.com/vacancies/1000101085", date));
        int id = first.getId();
        assertThat(store.findById(id), is(first));
    }

    @Test
    public void whenFindByIdFalse() {
        PsqlStore store = new PsqlStore(connection);
        assertThat(store.findById(0), is(nullValue()));
    }
}