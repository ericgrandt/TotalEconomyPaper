package com.ericgrandt.totaleconomy.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BalanceData {
    private final Database database;

    public BalanceData(Database database) {
        this.database = database;
    }

    public BigDecimal getBalance(UUID accountId, int currencyId) throws SQLException {
        String getDefaultBalanceQuery = "SELECT balance FROM te_balance WHERE account_id = ? AND currency_id = ?";

        try (
            Connection conn = database.getConnection();
            PreparedStatement stmt = conn.prepareStatement(getDefaultBalanceQuery)
        ) {
            stmt.setString(1, accountId.toString());
            stmt.setInt(2, currencyId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }

        return null;
    }
}
