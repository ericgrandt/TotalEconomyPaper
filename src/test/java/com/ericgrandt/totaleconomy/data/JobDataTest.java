package com.ericgrandt.totaleconomy.data;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.dto.BalanceDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JobDataTest {
    @Test
    @Tag("Unit")
    public void updateExperienceForJob_WithRowAffected_ShouldReturnOne() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(1);

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(UUID.randomUUID(), UUID.randomUUID(), 10);
        int expected = 1;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Unit")
    public void updateExperienceForJob_WithNoRowsAffected_ShouldReturnZero() throws SQLException {
        // Arrange
        Database databaseMock = mock(Database.class);
        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeUpdate()).thenReturn(0);

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(UUID.randomUUID(), UUID.randomUUID(), 10);
        int expected = 0;

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    @Tag("Integration")
    public void updateExperienceForJob_ShouldUpdateExperience() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID accountId = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");
        UUID jobId = UUID.fromString("a56a5842-1351-4b73-a021-bcd531260cd1");

        Database databaseMock = mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(TestUtils.getConnection());

        JobData sut = new JobData(databaseMock);

        // Act
        int actual = sut.updateExperienceForJob(accountId, jobId, 60);
        int expected = 1;

        JobExperienceDto actualJobExperienceDto = TestUtils.getExperienceForJob(accountId, jobId);
        JobExperienceDto expectedJobExperienceDto = new JobExperienceDto(
            "748af95b-32a0-45c2-bfdc-9e87c023acdf",
            accountId.toString(),
            jobId.toString(),
            60
        );

        // Assert
        assertEquals(expected, actual);
        assertEquals(expectedJobExperienceDto, actualJobExperienceDto);
    }
}
