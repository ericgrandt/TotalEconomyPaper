package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;

import java.math.BigDecimal;
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

    // public JobRewardDto getJobReward() {
    //
    // }
}
