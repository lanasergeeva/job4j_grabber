package ru.job4j.grabber;

import org.postgresql.util.PSQLException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Connection cnn) {
        this.cnn = cnn;
    }

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver_class"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("insert into post (name, text, link, created) values (?, ?, ?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getName());
            statement.setString(2, post.getText());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (PSQLException p) {
            System.out.println("Дубликат");
            p.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    postList.add(new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post rsl = null;
        try (
                PreparedStatement statement =
                        cnn.prepareStatement("select * from post where id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    rsl = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getTimestamp("created").toLocalDateTime()
                    );
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return rsl;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(
                "./src/main/resources/app.properties")) {
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        PsqlStore psqlStore = new PsqlStore(properties);
        SqlParseSite sql = new SqlParseSite();
        Post post;
        Post post3 = sql.detail("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
        Post post2 = sql.detail("https://www.sql.ru/forum/1323839/razrabotchik-java-g-kazan");
        // psqlStore.save(post);
        //psqlStore.save(post3);
        List<Post> list = psqlStore.getAll();
        System.out.println(list.toString());
        post = psqlStore.findById(2);
        System.out.println(post);
    }
}
