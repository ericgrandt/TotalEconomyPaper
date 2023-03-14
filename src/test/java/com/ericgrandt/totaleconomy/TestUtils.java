package com.ericgrandt.totaleconomy;

import com.ericgrandt.totaleconomy.data.dto.AccountDto;
import com.ericgrandt.totaleconomy.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtils {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:h2:mem:totaleconomy;MODE=MySQL");
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
            String insertDefaultBalance = "INSERT INTO te_default_balance\n"
                + "VALUES('05231a59-b6fa-4d57-8450-5bd07f148a98', 1, 100.50);";

            Statement statement = conn.createStatement();
            statement.execute(insertDefaultBalance);
        }
    }

    public static void seedJobs() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJob1 = "INSERT INTO te_job VALUES('a56a5842-1351-4b73-a021-bcd531260cd1', 'Test Job 1');";
            String insertJob2 = "INSERT INTO te_job VALUES('858febd0-7122-4ea4-b270-a69a4b6a53a4', 'Test Job 2');";

            Statement statement = conn.createStatement();
            statement.execute(insertJob1);
            statement.execute(insertJob2);
        }
    }

    public static void seedJobExperience() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJobExperience1 = "INSERT INTO te_job_experience "
                + "VALUES('748af95b-32a0-45c2-bfdc-9e87c023acdf', '62694fb0-07cc-4396-8d63-4f70646d75f0', 'a56a5842-1351-4b73-a021-bcd531260cd1', 50);";
            String insertJobExperience2 = "INSERT INTO te_job_experience "
                + "VALUES('6cebc95b-7743-4f63-92c6-0fd0538d8b0c', '62694fb0-07cc-4396-8d63-4f70646d75f0', '858febd0-7122-4ea4-b270-a69a4b6a53a4', 10);";

            Statement statement = conn.createStatement();
            statement.execute(insertJobExperience1);
            statement.execute(insertJobExperience2);
        }
    }

    public static void seedJobActions() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJobAction = "INSERT INTO te_job_action "
                + "VALUES('fbc60ff9-d7e2-4704-9460-6edc2e7b6066', 'break');";

            Statement statement = conn.createStatement();
            statement.execute(insertJobAction);
        }
    }

    public static void seedJobRewards() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String insertJobReward = "INSERT INTO te_job_reward "
                + "VALUES('07ac5e1f-39ef-46a8-ad81-a4bc1facc090', 'a56a5842-1351-4b73-a021-bcd531260cd1', "
                + "'fbc60ff9-d7e2-4704-9460-6edc2e7b6066', 1, 'coal_ore', 0.50, 1);";

            Statement statement = conn.createStatement();
            statement.execute(insertJobReward);
        }
    }

    public static void resetDb() throws SQLException {
        try (Connection conn = TestUtils.getConnection()) {
            String deleteCurrencies = "DELETE FROM te_currency";
            String deleteUsers = "DELETE FROM te_account";
            String deleteBalances = "DELETE FROM te_balance";
            String deleteDefaultBalances = "DELETE FROM te_default_balance";
            String deleteJobs = "DELETE FROM te_job";
            String deleteJobExperience = "DELETE FROM te_job_experience";
            String deleteJobActions = "DELETE FROM te_job_action";
            String deleteJobRewards = "DELETE FROM te_job_reward";

            Statement statement = conn.createStatement();
            statement.execute(deleteCurrencies);
            statement.execute(deleteUsers);
            statement.execute(deleteBalances);
            statement.execute(deleteDefaultBalances);
            statement.execute(deleteJobs);
            statement.execute(deleteJobExperience);
            statement.execute(deleteJobActions);
            statement.execute(deleteJobRewards);
        }
    }

    public static AccountDto getAccount(UUID accountId) throws SQLException {
        String query = "SELECT * FROM te_account WHERE id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new AccountDto(
                            rs.getString("id"),
                            rs.getTimestamp("created")
                        );
                    }

                    return null;
                }
            }
        }
    }

    public static BalanceDto getBalanceForAccountId(UUID accountId, int currencyId) throws SQLException {
        String query = "SELECT * FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());
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

    public static JobExperienceDto getExperienceForJob(UUID accountId, UUID jobId) throws SQLException {
        String query = "SELECT * FROM te_job_experience WHERE account_id = ? AND job_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());
                stmt.setString(2, jobId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new JobExperienceDto(
                            rs.getString("id"),
                            rs.getString("account_id"),
                            rs.getString("job_id"),
                            rs.getInt("experience")
                        );
                    }

                    return null;
                }
            }
        }
    }

    public static List<JobExperienceDto> getExperienceForJobs(UUID accountId) throws SQLException {
        String query = "SELECT account_id, job_id, experience FROM te_job_experience WHERE account_id = ?";

        try (Connection conn = TestUtils.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, accountId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    List<JobExperienceDto> jobExperienceDtos = new ArrayList<>();
                    while (rs.next()) {
                        jobExperienceDtos.add(
                            new JobExperienceDto(
                                "",
                                rs.getString("account_id"),
                                rs.getString("job_id"),
                                rs.getInt("experience")
                            )
                        );
                    }

                    return jobExperienceDtos;
                }
            }
        }
    }
}
