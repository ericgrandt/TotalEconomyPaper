package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class JobData {
    private final Database database;

    public JobData(Database database) {
        this.database = database;
    }

    public JobExperienceDto getExperienceForJob(UUID accountId, UUID jobId) throws SQLException {
        String getDefaultBalanceQuery = "SELECT * FROM te_job_experience WHERE account_id = ? AND job_id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
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
            }
        }

        return null;
    }

    public JobRewardDto getJobReward(String jobActionId, String material) throws SQLException {
        String getDefaultBalanceQuery = "SELECT * FROM te_job_reward "
            + "WHERE job_action_id = ? AND material = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, jobActionId);
            stmt.setString(2, material);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new JobRewardDto(
                        rs.getString("id"),
                        rs.getString("job_id"),
                        rs.getString("job_action_id"),
                        rs.getInt("currency_id"),
                        rs.getString("material"),
                        rs.getBigDecimal("money"),
                        rs.getInt("experience")
                    );
                }
            }
        }

        return null;
    }

    public JobActionDto getJobActionByName(String jobActionName) throws SQLException {
        String getDefaultBalanceQuery = "SELECT * FROM te_job_action WHERE action_name = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, jobActionName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new JobActionDto(
                        rs.getString("id"),
                        rs.getString("action_name")
                    );
                }
            }
        }

        return null;
    }

    public void createJobExperienceRows(UUID accountId) throws SQLException {
        String createBalanceQuery = "INSERT INTO te_job_experience(account_id, job_id) "
            + "SELECT ?, j.id FROM te_job j ON DUPLICATE KEY UPDATE account_id = account_id";

        try (Connection conn = database.getConnection()) {
            try (PreparedStatement accountStmt = conn.prepareStatement(createBalanceQuery)) {
                accountStmt.setString(1, accountId.toString());
                accountStmt.executeUpdate();
            }
        }
    }

    public int updateExperienceForJob(UUID accountId, UUID jobId, int experience) throws SQLException {
        String updateExperienceForJobQuery = "UPDATE te_job_experience SET experience = ? WHERE account_id = ? AND job_id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(updateExperienceForJobQuery)
        ) {
            stmt.setInt(1, experience);
            stmt.setString(2, accountId.toString());
            stmt.setString(3, jobId.toString());

            return stmt.executeUpdate();
        }
    }
}
