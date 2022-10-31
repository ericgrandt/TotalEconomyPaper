package com.ericgrandt.totaleconomy.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
}
