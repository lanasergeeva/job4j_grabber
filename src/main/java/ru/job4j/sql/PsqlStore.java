package ru.job4j.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class);
    private Connection cnn;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("username"),
                    cfg.getProperty("password")
            );
        } catch (SQLException e) {
            LOG.error(" Ошибка в конструкторе", e);
        }
    }

    public Post getPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("text"),
                resultSet.getString("link"),
                resultSet.getTimestamp("created").toLocalDateTime()
        );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("INSERT INTO POST (NAME , TEXT, link, created) VALUES (?, ?, ?, ?)"
                                     + "VALUES (?, ? ,?, ?) on conflict (link) do nothing",
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
        } catch (Exception e) {
            LOG.error("Ошибка в save", e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement statement = cnn.prepareStatement("SELECT * from POST")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    postList.add(getPost(resultSet));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка в getAll", e);
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
                    rsl = getPost(resultSet);
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка в findById", e);
        }
        return rsl;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
