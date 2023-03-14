package com.ericgrandt.totaleconomy.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DatabaseTest {
    @Test
    @Tag("Integration")
    public void getConnection_ShouldCreateDatabaseConnection() throws SQLException {
        // Arrange
        Database sut = new Database("jdbc:h2:mem:totaleconomy", "", "");
        String query = "SELECT 1";

        // Act/Assert
        try (
            Connection conn = sut.getDataSource().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            rs.next();
            int actual = rs.getInt(1);
            int expected = 1;

            assertEquals(expected, actual);
        }
    }
}
