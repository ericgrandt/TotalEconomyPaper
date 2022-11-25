package com.ericgrandt.totaleconomy.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class JobData {
    private final Database database;

    public JobData(Database database) {
        this.database = database;
    }

    public int getExperienceForJob(UUID accountId, UUID jobId) {
        return 0;
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
