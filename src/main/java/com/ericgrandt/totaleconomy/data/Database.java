package com.ericgrandt.totaleconomy.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.ibatis.jdbc.ScriptRunner;

public class Database {
    private final String url;
    private final String user;
    private final String password;

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        String connectionString = String.format(
            "%s?user=%s&password=%s",
            url,
            user,
            password
        );
        return DriverManager.getConnection(connectionString);
    }

    public void initDatabase() throws SQLException, IOException {
        try (
            Connection conn = getConnection();
            InputStream is = getClass().getResourceAsStream("/schema.sql");
            InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(is))
        ) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.setLogWriter(null);
            runner.runScript(reader);
        }
    }
}
