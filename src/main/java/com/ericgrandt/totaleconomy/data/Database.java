package com.ericgrandt.totaleconomy.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class Database {
    private final String url;
    private final String user;
    private final String password;
    private final HikariDataSource dataSource;

    public Database(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.dataSource = createDataSource();
    }

    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(this.user);
        config.setPassword(this.password);
        config.addDataSourceProperty("minimumIdle", "3");
        config.addDataSourceProperty("maximumPoolSize", "10");
        return new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}
