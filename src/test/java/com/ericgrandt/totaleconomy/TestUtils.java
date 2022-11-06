package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.data.dto.BalanceDto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.apache.ibatis.jdbc.ScriptRunner;

public class TestUtils {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:totaleconomy");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
        setupDb();
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static void setupDb() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("schema.sql");
        try (Connection conn = TestUtils.getConnection();
             InputStreamReader reader = new InputStreamReader(is)
        ) {
            ScriptRunner runner = new ScriptRunner(conn);
            runner.runScript(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void seedCurrencies() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDollarCurrency = "INSERT INTO te_currency\n"
                + "VALUES(1, 'Dollar', 'Dollars', '$', 0, true)";

            Statement statement = conn.createStatement();
            statement.execute(insertDollarCurrency);
        }
    }

    public static void seedAccounts() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertAccount = "INSERT INTO te_account\n"
                + "VALUES('62694fb0-07cc-4396-8d63-4f70646d75f0', '2022-01-01 00:00:00');";
            String insertBalance = "INSERT INTO te_balance\n"
                + "VALUES('ab661384-11f5-41e1-a5e6-6fa93305d4d1', '62694fb0-07cc-4396-8d63-4f70646d75f0', 1, 50)";
            String insertAccount2 = "INSERT INTO te_account\n"
                + "VALUES('551fe9be-f77f-4bcb-81db-548db6e77aea', '2022-01-02 00:00:00');";
            String insertBalance2 = "INSERT INTO te_balance\n"
                + "VALUES('a766cedf-f53e-450d-804a-4f292357938f', '551fe9be-f77f-4bcb-81db-548db6e77aea', 1, 100)";

            Statement statement = conn.createStatement();
            statement.execute(insertAccount);
            statement.execute(insertBalance);
            statement.execute(insertAccount2);
            statement.execute(insertBalance2);
        }
    }

    public static void seedDefaultBalances() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertDefaultBalance1 = "INSERT INTO te_default_balance\n"
                + "VALUES('05231a59-b6fa-4d57-8450-5bd07f148a98', 1, 100.50);";

            Statement statement = conn.createStatement();
            statement.execute(insertDefaultBalance1);
        }
    }

    public static void resetDb() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String deleteCurrencies = "DELETE FROM te_currency";
            String deleteUsers = "DELETE FROM te_account";
            String deleteBalances = "DELETE FROM te_balance";
            String deleteDefaultBalances = "DELETE FROM te_default_balance";

            Statement statement = conn.createStatement();
            statement.execute(deleteCurrencies);
            statement.execute(deleteUsers);
            statement.execute(deleteBalances);
            statement.execute(deleteDefaultBalances);
        }
    }

    public static BalanceDto getBalanceForAccountId(UUID accountUUID, int currencyId) throws SQLException {
        String query = "SELECT * FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountUUID.toString());
                stmt.setInt(2, currencyId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new BalanceDto(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getInt("currency_id"),
                            rs.getBigDecimal("balance")
                        );
                    }

                    return null;
                }
            }
        }
    }
}
