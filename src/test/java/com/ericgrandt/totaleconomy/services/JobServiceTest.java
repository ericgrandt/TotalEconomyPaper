package com.ericgrandt.totaleconomy.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobExperienceDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobServiceTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void getJobReward_WithRewardFound_ShouldReturnJobRewardDto() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenReturn(jobReward);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertEquals(jobReward, actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithNoRewardFound_ShouldReturnNull() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSqlException_ShouldReturnNull() throws SQLException {
        // Arrange
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        JobRewardDto actual = sut.getJobReward("break", jobReward.material());

        // Assert
        assertNull(actual);
    }

    @Test
    @Tag("Unit")
    public void getJobReward_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        JobActionDto jobAction = new JobActionDto("id", "break");
        JobRewardDto jobReward = new JobRewardDto(
            "",
            "",
            "",
            1,
            "material",
            BigDecimal.TEN,
            1
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getJobActionByName("break")).thenReturn(jobAction);
        when(jobDataMock.getJobReward(jobAction.id(), jobReward.material())).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.getJobReward("break", jobReward.material());

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(
                String.format(
                    "[Total Economy] Error calling getJobReward (actionName: %s, materialName: %s)",
                    "break",
                    jobReward.material()
                )
            ),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceForAccount_WithSuccess_ShouldCallJobData() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        JobData jobDataMock = mock(JobData.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.createJobExperienceForAccount(accountId);

        // Assert
        verify(jobDataMock, times(1)).createJobExperienceRows(accountId);
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceForAccount_WithSqlException_ShouldLogError() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();

        JobData jobDataMock = mock(JobData.class);
        doThrow(SQLException.class).when(jobDataMock).createJobExperienceRows(accountId);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.createJobExperienceForAccount(accountId);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(
                String.format(
                    "[Total Economy] Error calling createJobExperienceForAccount (accountId: %s)",
                    accountId
                )
            ),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceFoundForAccount_ShouldUpdateExperience() throws SQLException {
        // Arrange
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        int experienceToAdd = 1;
        JobExperienceDto jobExperienceDto = new JobExperienceDto(
            "id",
            accountId.toString(),
            jobId.toString(),
            10
        );

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(jobExperienceDto);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(jobDataMock, times(1)).updateExperienceForJob(
            accountId,
            jobId,
            jobExperienceDto.experience() + experienceToAdd
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceNotFoundForAccount_ShouldNotUpdateExperience() throws SQLException {
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        int experienceToAdd = 1;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(jobDataMock, times(0)).updateExperienceForJob(
            any(UUID.class),
            any(UUID.class),
            any(Integer.class)
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithJobExperienceNotFoundForAccount_ShouldLogWarning() throws SQLException {
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        int experienceToAdd = 1;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenReturn(null);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.WARNING),
            eq(String.format(
                "[Total Economy] No job experience entry found for user (accountId: %s, jobId: %s)",
                accountId,
                jobId
            ))
        );
    }

    @Test
    @Tag("Unit")
    public void addExperience_WithSQLException_ShouldLogError() throws SQLException {
        UUID accountId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        int experienceToAdd = 1;

        JobData jobDataMock = mock(JobData.class);
        when(jobDataMock.getExperienceForJob(accountId, jobId)).thenThrow(SQLException.class);

        JobService sut = new JobService(loggerMock, jobDataMock);

        // Act
        sut.addExperience(accountId, jobId, experienceToAdd);

        // Assert
        verify(loggerMock, times(1)).log(
            eq(Level.SEVERE),
            eq(String.format(
                "[Total Economy] Error calling addExperience (accountId: %s, jobId: %s, experience: %s)",
                accountId,
                jobId,
                experienceToAdd
            )),
            any(SQLException.class)
        );
    }
}
