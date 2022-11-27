package com.ericgrandt.totaleconomy.listeners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ericgrandt.totaleconomy.data.dto.JobRewardDto;
import com.ericgrandt.totaleconomy.impl.EconomyImpl;
import com.ericgrandt.totaleconomy.services.JobService;
import java.math.BigDecimal;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JobListenerTest {
    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFound_ShouldDepositMoney() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "de8ee82d-e988-4b6e-8dfd-8768415e4a0d",
            "",
            1,
            materialName,
            BigDecimal.TEN,
            1
        );

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(economyMock, times(1)).depositPlayer(playerMock, BigDecimal.TEN.doubleValue());
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithJobRewardFound_ShouldAddExperience() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();
        UUID playerId = UUID.randomUUID();
        JobRewardDto jobRewardDto = new JobRewardDto(
            "",
            "de8ee82d-e988-4b6e-8dfd-8768415e4a0d",
            "",
            1,
            materialName,
            BigDecimal.TEN,
            1
        );

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(playerMock.getUniqueId()).thenReturn(playerId);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(jobRewardDto);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(jobServiceMock, times(1)).addExperience(
            playerId,
            UUID.fromString(jobRewardDto.jobId()),
            jobRewardDto.experience()
        );
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNoJobRewardFound_ShouldNotDepositMoney() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(null);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(economyMock, times(0)).depositPlayer(any(Player.class), any(Double.class));
    }

    @Test
    @Tag("Unit")
    public void onBreakAction_WithNoJobRewardFound_ShouldNotAddExperience() {
        // Arrange
        Material material = Material.STONE;
        String materialName = material.name().toLowerCase();

        Block blockMock = mock(Block.class);
        Player playerMock = mock(Player.class);
        EconomyImpl economyMock = mock(EconomyImpl.class);
        JobService jobServiceMock = mock(JobService.class);
        when(blockMock.getType()).thenReturn(material);
        when(jobServiceMock.getJobReward("break", materialName)).thenReturn(null);

        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(blockMock, playerMock);
        JobListener sut = new JobListener(economyMock, jobServiceMock);

        // Act
        sut.onBreakAction(blockBreakEvent);

        // Assert
        verify(jobServiceMock, times(0)).addExperience(
            any(UUID.class),
            any(UUID.class),
            any(Integer.class)
        );
    }

    @Test
    @Tag("Unit")
    public void createJobExperienceOnPlayerJoin_ShouldCallTheJobService() {
        // Arrange
        UUID playerId = UUID.randomUUID();

        Player playerMock = mock(Player.class);
        JobService jobServiceMock = mock(JobService.class);
        when(playerMock.getUniqueId()).thenReturn(playerId);

        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(playerMock, "");
        JobListener sut = new JobListener(mock(EconomyImpl.class), jobServiceMock);

        // Act
        sut.createJobExperienceOnPlayerJoin(playerJoinEvent);

        // Assert
        verify(jobServiceMock, times(1)).createJobExperienceForAccount(playerId);
    }
}
