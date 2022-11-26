package com.ericgrandt.totaleconomy.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.data.dto.JobActionDto;
import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import java.math.BigDecimal;
import java.sql.SQLException;
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
}
