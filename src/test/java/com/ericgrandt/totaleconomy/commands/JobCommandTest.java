package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.TestUtils;
import com.ericgrandt.totaleconomy.data.Database;
import com.ericgrandt.totaleconomy.data.JobData;
import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobCommandTest {
    @Mock
    private Logger loggerMock;

    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        JobCommand sut = new JobCommand(loggerMock, mock(JobService.class));

        // Act
        boolean actual = sut.onCommand(
            mock(ConsoleCommandSender.class),
            mock(Command.class),
            "",
            null
        );

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithSuccess_ShouldReturnTrue() {
        // Arrange
        JobCommand sut = new JobCommand(loggerMock, mock(JobService.class));

        // Act
        boolean actual = sut.onCommand(
            mock(Player.class),
            mock(Command.class),
            "",
            null
        );

        // Assert
        assertTrue(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithSuccess_ShouldSendPlayerMessage() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();
        List<JobExperience> jobExperienceList = List.of(
            new JobExperience("job1", 0, 10, 1),
            new JobExperience("job2", 35, 50, 3)
        );

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenReturn(jobExperienceList);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommand(
            playerMock,
            mock(Command.class),
            "",
            null
        );
        Component expected = Component.empty()
            .content("\n")
            .append(Component.text("job1", NamedTextColor.GRAY, TextDecoration.BOLD))
            .append(Component.text(" [LVL 1]", NamedTextColor.GRAY))
            .append(Component.text(" [0/10 EXP]", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("job2", NamedTextColor.GRAY, TextDecoration.BOLD))
            .append(Component.text(" [LVL 3]", NamedTextColor.GRAY))
            .append(Component.text(" [35/50 EXP]", NamedTextColor.GRAY))
            .append(Component.newline());

        // Assert
        verify(playerMock, times(1)).sendMessage(expected);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithException_ShouldReturnFalse() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenThrow(SQLException.class);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        boolean actual = sut.onCommand(
            playerMock,
            mock(Command.class),
            "",
            null
        );

        // Assert
        assertFalse(actual);
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithException_ShouldSendPlayerMessage() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenThrow(SQLException.class);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommand(
            playerMock,
            mock(Command.class),
            "",
            null
        );

        // Assert
        verify(playerMock).sendMessage(
            Component.text("An error has occurred. Please contact an administrator.", NamedTextColor.RED)
        );
    }

    @Test
    @Tag("Unit")
    public void onCommand_WithException_ShouldLogException() throws SQLException {
        // Arrange
        UUID playerUuid = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerUuid);
        when(jobServiceMock.getExperienceForAllJobs(playerUuid)).thenThrow(SQLException.class);

        JobCommand sut = new JobCommand(loggerMock, jobServiceMock);

        // Act
        sut.onCommand(
            playerMock,
            mock(Command.class),
            "",
            null
        );

        // Assert
        verify(loggerMock).log(
            eq(Level.SEVERE),
            eq("An exception occurred during the handling of the job command."),
            any(SQLException.class)
        );
    }

    @Test
    @Tag("Integration")
    public void onCommand_ShouldSendMessageWithJobLevelsToPlayer() throws SQLException {
        // Arrange
        TestUtils.resetDb();
        TestUtils.seedCurrencies();
        TestUtils.seedAccounts();
        TestUtils.seedJobs();
        TestUtils.seedJobExperience();

        UUID playerUuid = UUID.fromString("62694fb0-07cc-4396-8d63-4f70646d75f0");

        Database databaseMock = mock(Database.class);
        CommandSender senderMock = mock(Player.class);
        when(databaseMock.getConnection()).then(x -> TestUtils.getConnection());
        when(((OfflinePlayer) senderMock).getUniqueId()).thenReturn(playerUuid);

        JobData jobData = new JobData(databaseMock);
        JobService jobService = new JobService(loggerMock, jobData);
        JobCommand sut = new JobCommand(loggerMock, jobService);

        // Act
        boolean actual = sut.onCommand(senderMock, mock(Command.class), "", null);
        Component expectedMessage = Component.empty()
            .content("\n")
            .append(Component.text("Test Job 1", NamedTextColor.GRAY, TextDecoration.BOLD))
            .append(Component.text(" [LVL 2]", NamedTextColor.GRAY))
            .append(Component.text(" [50/197 EXP]", NamedTextColor.GRAY))
            .append(Component.newline())
            .append(Component.text("Test Job 2", NamedTextColor.GRAY, TextDecoration.BOLD))
            .append(Component.text(" [LVL 1]", NamedTextColor.GRAY))
            .append(Component.text(" [10/50 EXP]", NamedTextColor.GRAY))
            .append(Component.newline());

        // Assert
        verify(senderMock).sendMessage(expectedMessage);
        assertTrue(actual);
    }
}
