package com.ericgrandt.totaleconomy.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.models.JobExperience;
import com.ericgrandt.totaleconomy.services.JobService;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class JobCommandTest {
    @Test
    @Tag("Unit")
    public void onCommand_WithNonPlayerSender_ShouldReturnFalse() {
        // Arrange
        JobCommand sut = new JobCommand(mock(JobService.class));

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
        JobCommand sut = new JobCommand(mock(JobService.class));

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

        JobCommand sut = new JobCommand(jobServiceMock);

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
    public void onCommand_WithException_ShouldSendPlayerMessage() {

    }

    @Test
    @Tag("Unit")
    public void onCommand_WithException_ShouldLogException() {

    }
}
