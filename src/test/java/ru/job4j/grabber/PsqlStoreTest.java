package ru.job4j.grabber;


import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileInputStream;
import java.io.IOException;


public class PsqlStoreTest {
    public Connection cn;

    public Connection init() {
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream(
                "./src/main/resources/app.properties")) {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName(config.getProperty("driver_class"));
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
        try {
            cn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return cn;
    }


    @Test
    public void whileSaveAndGetAll() {
        String temp = null;
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            store.save(new Post("name", "text", "//http", LocalDateTime.now()));
            List<Post> post = store.getAll();
            for (Post posts : post) {
                if (posts.getLink().equals("//http")) {
                    temp = "//http";
                    break;
                }
            }
            assertThat(temp, is("//http"));
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    @Test
    public void findItemById() {
        try (PsqlStore store = new PsqlStore(ConnectionRollback.create(this.init()))) {
            Post post = new Post("name", "text", "//http",
                    LocalDateTime.of(1999, 5, 4, 14, 55));
            store.save(post);
            assertThat(store.findById(post.getId()), is(post));
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}

