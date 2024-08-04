package com.artemistechnica.commons.datastores.postgres;

import com.artemistechnica.commons.datatypes.EitherE;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgresRepoTests implements PostgresRepo<PostgresRepoTests.SimpleUser> {

    private final String url = "jdbc:postgresql://localhost:5432/mydatabase";
    private final String user = "myuser";
    private final String password = "mypassword";

    private Connection conn;

    @SneakyThrows
    @Override
    public Connection connection() {
        if (conn == null) {
            conn = DriverManager.getConnection(url, user, password);
        }
        return conn;
    }

    @Test
    public void testPostgresConnection() {
        EitherE<SimpleUser> result = create("INSERT INTO users (id, name) VALUES ('abc', 'Test Man')", new PCreate(), new SimpleUser("abc", "Test User"));
    }

    public static class SimpleUser {
        public String id;
        public String name;

        public SimpleUser(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public SimpleUser() {

        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
